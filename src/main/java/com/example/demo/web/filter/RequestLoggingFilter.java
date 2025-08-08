package com.example.demo.web.filter;

import com.example.demo.config.DemoProperties;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final DemoProperties demoProperties;
    private final ConcurrentHashMap<Long, AtomicInteger> minuteToCount = new ConcurrentHashMap<>();

    public RequestLoggingFilter(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long minuteKey = Instant.now().getEpochSecond() / 60;
        AtomicInteger count = minuteToCount.computeIfAbsent(minuteKey, k -> new AtomicInteger(0));
        int current = count.incrementAndGet();
        if (current > demoProperties.getRateLimiting().getMaxRequestsPerMinute()) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return;
        }
        log.debug("{} {}", request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}


