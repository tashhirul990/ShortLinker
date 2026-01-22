package com.shortLinker.ShortLinker.service.impl;

import com.shortLinker.ShortLinker.entity.UrlMapping;
import com.shortLinker.ShortLinker.repository.UrlRepository;
import com.shortLinker.ShortLinker.service.UrlService;
import com.shortLinker.ShortLinker.utilty.Base62Encoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final Logger LOGGER = LoggerFactory.getLogger(UrlServiceImpl.class);
    @Autowired
    private UrlRepository repository;

    @Override
    public String shortenUrl(String longUrl) {
        Optional<String> existingShortKey = repository.findShortKeyByLongUrl(longUrl);
        if (existingShortKey.isPresent()) {
            LOGGER.info("Long URL already shortened: {}", existingShortKey.get());
            return existingShortKey.get();
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

    @Override
    public String getOriginalUrl(String shortKey) {
        byte[] bytes = repository.findLongUrlByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("URL not found"))
                .getBytes(StandardCharsets.UTF_8);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
