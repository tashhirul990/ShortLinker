package com.shortLinker.ShortLinker.service;

import com.shortLinker.ShortLinker.DTO.RedisValues;

public interface RedisCacheService {
    public void saveRedisValues(String shortKey, RedisValues redisValues, long ttlSeconds);

    public RedisValues getRedisValues(String shortKey);

    public void deleteRedisValues(String shortKey);
}
