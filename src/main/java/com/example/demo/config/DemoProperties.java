package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo")
public class DemoProperties {
    private RateLimiting rateLimiting = new RateLimiting();

    public RateLimiting getRateLimiting() {
        return rateLimiting;
    }

    public void setRateLimiting(RateLimiting rateLimiting) {
        this.rateLimiting = rateLimiting;
    }

    public static class RateLimiting {
        private int maxRequestsPerMinute = 60;

        public int getMaxRequestsPerMinute() {
            return maxRequestsPerMinute;
        }

        public void setMaxRequestsPerMinute(int maxRequestsPerMinute) {
            this.maxRequestsPerMinute = maxRequestsPerMinute;
        }
    }
}


