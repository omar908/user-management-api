package com.omar.user_management_api.web.filter;

import com.omar.user_management_api.config.RateLimitingProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private final String LOG_FOR_NUMBER_OF_REQUESTS = "Requests between {} and {} UTC: {} requests.";;

    private final RateLimitingProperties rateLimitingProperties;
    private final ConcurrentHashMap<Long, AtomicInteger> minuteToCount = new ConcurrentHashMap<>();

    public RequestLoggingFilter(RateLimitingProperties rateLimitingProperties) {
        this.rateLimitingProperties = rateLimitingProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long minuteKey = Instant.now().getEpochSecond() / 60;

        // For the stale/older minutes, logs the data and removes them from memory
        minuteToCount.keySet().removeIf(key -> {
            if (key < minuteKey) {
                AtomicInteger count = minuteToCount.get(key);

                // Convert numeric minuteKey back to UTC time for logging
                Instant minuteStart = Instant.ofEpochSecond(key * 60);
                Instant minuteEnd = minuteStart.plusSeconds(60);

                ZonedDateTime startUtc = minuteStart.atZone(ZoneOffset.UTC);
                ZonedDateTime endUtc = minuteEnd.atZone(ZoneOffset.UTC);

                log.info(
                        LOG_FOR_NUMBER_OF_REQUESTS, startUtc.toLocalDateTime(), endUtc.toLocalDateTime(), count
                );

                return true;
            }
            return false;
        });

        AtomicInteger count = minuteToCount.computeIfAbsent(minuteKey, k -> new AtomicInteger(0));
        int current = count.incrementAndGet();
        if (current > rateLimitingProperties.getRateLimiting().getMaxRequestsPerMinute()) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return;
        }
        log.debug("{} {}", request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}


