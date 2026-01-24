package com.shortLinker.ShortLinker.service.impl;

import com.shortLinker.ShortLinker.DTO.RedisValues;
import com.shortLinker.ShortLinker.service.RedisCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@AllArgsConstructor
@Service
@Slf4j
public class RedisCacheServiceImpl implements RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Save URL in Redis
    public void saveRedisValues(String shortKey, RedisValues redisValues, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(shortKey, redisValues, Duration.ofSeconds(ttlSeconds));
    }

    // Get URL from Redis
    public RedisValues getRedisValues(String shortKey) {
        Map<String, Object> object = (Map<String, Object>) redisTemplate.opsForValue().get(shortKey);
        if (object == null) {
            return null;
        }
        String longUrl = (String) object.get("longUrl");
        Double epochSeconds = (Double) object.get("createdAt");
        OffsetDateTime createdAt = OffsetDateTime.ofInstant(
                Instant.ofEpochSecond(epochSeconds.longValue()),
                ZoneOffset.UTC
        );

        RedisValues redisValues = RedisValues.builder()
                .longUrl(longUrl)
                .createdAt(createdAt)
                .build();
        return redisValues;
    }

    // Delete URL from Redis
    public void deleteRedisValues(String shortKey) {
        redisTemplate.delete(shortKey);
    }
}
