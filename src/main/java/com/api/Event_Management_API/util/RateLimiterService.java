package com.api.Event_Management_API.util;

import java.time.Instant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private static class RequestCounter {
        int requestCount;
        long startTime;

        RequestCounter(int requestCount, long startTime) {
            this.requestCount = requestCount;
            this.startTime = startTime;
        }
    }

    private final Map<String, RequestCounter> requestMap = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 5;
    private final long WINDOW_SIZE_MS = 1000 * 60; // 1 minute

    public boolean isAllowed(String key) {
        long currentTime = Instant.now().toEpochMilli();
    
        return requestMap.compute(key, (k, counter) -> {
            if (counter == null || currentTime - counter.startTime > WINDOW_SIZE_MS) {
                return new RequestCounter(1, currentTime);
            }
    
            counter.requestCount++;
            return counter;
        }).requestCount <= MAX_REQUESTS;
    }
    
}
