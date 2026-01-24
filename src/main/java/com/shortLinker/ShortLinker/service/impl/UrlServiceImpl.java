package com.shortLinker.ShortLinker.service.impl;

import com.shortLinker.ShortLinker.DTO.RedisValues;
import com.shortLinker.ShortLinker.entity.UrlMapping;
import com.shortLinker.ShortLinker.repository.UrlRepository;
import com.shortLinker.ShortLinker.service.RedisCacheService;
import com.shortLinker.ShortLinker.service.UrlService;
import com.shortLinker.ShortLinker.utilty.Base62Encoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final Logger LOGGER = LoggerFactory.getLogger(UrlServiceImpl.class);
    @Autowired
    private UrlRepository repository;

    @Autowired
    private RedisCacheService redisCacheService;

    @Value("${app.expriration-days}")
    private Integer exparationPeriod;

    @Value("${app.cache.ttl}")
    private Integer cacheTtlInSeconds;

    @Override
    public String shortenUrl(String longUrl) {
        Optional<UrlMapping> existingUrlMapping = repository.findByLongUrl(longUrl);
        if (existingUrlMapping.isPresent()) {
            LOGGER.info("Long URL already shortened: {}", existingUrlMapping.get().getShortKey());
            updateExistingUrlAccessTime(existingUrlMapping.get());
            return existingUrlMapping.get().getShortKey();
        }
        UrlMapping entity = new UrlMapping();
        entity.setLongUrl(longUrl);
        entity.setCreatedAt(OffsetDateTime.now());

        entity = repository.save(entity); // generates ID

        String shortKey = Base62Encoder.encode(entity.getId());
        entity.setShortKey(shortKey);

        repository.save(entity);
        return shortKey;
    }

    private void updateExistingUrlAccessTime(UrlMapping urlMapping) {
        if (urlMapping.getCreatedAt().isBefore(OffsetDateTime.now().minusDays(exparationPeriod / 2))) {
            urlMapping.setCreatedAt(OffsetDateTime.now());
            repository.save(urlMapping);
        }
    }

    @Override
    public String getOriginalUrl(String shortKey) {
        LOGGER.info("Retrieving original URL for shortKey: {}", shortKey);
        RedisValues redisValues = redisCacheService.getRedisValues(shortKey);
        if (redisValues != null) {
            LOGGER.info("Cache hit for shortKey: {}", shortKey);
            updateExistingUrlAccessTimeRedis(shortKey, redisValues);
            return redisValues.getLongUrl();
        }
        Optional<UrlMapping> urlMapping = Optional.ofNullable(repository.findByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("URL not found")));
        updateExistingUrlAccessTime(urlMapping.get());
        RedisValues newRedisValues = RedisValues.builder()
                .longUrl(urlMapping.get().getLongUrl())
                .createdAt(urlMapping.get().getCreatedAt())
                .build();
        redisCacheService.saveRedisValues(shortKey, newRedisValues, cacheTtlInSeconds);
        LOGGER.info("Cache miss for shortKey: {}. Fetched from DB and cached.", shortKey);
        return urlMapping.get().getLongUrl();
    }

    private void updateExistingUrlAccessTimeRedis(String shortKey, RedisValues redisValues) {
        if (redisValues.getCreatedAt().isBefore(OffsetDateTime.now().minusDays(exparationPeriod / 2))) {
            Optional<UrlMapping> urlMapping = repository.findByShortKey(shortKey);
            if (urlMapping.isPresent()) {
                urlMapping.get().setCreatedAt(OffsetDateTime.now());
                repository.save(urlMapping.get());
                redisValues.setCreatedAt(OffsetDateTime.now());
                redisCacheService.saveRedisValues(shortKey, redisValues, cacheTtlInSeconds);
                LOGGER.info("Updated access time for shortKey in Redis: {}", shortKey);
            }
        }
    }
}
