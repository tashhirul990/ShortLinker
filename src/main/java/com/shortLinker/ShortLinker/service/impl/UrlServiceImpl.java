package com.shortLinker.ShortLinker.service.impl;

import com.shortLinker.ShortLinker.entity.UrlMapping;
import com.shortLinker.ShortLinker.repository.UrlRepository;
import com.shortLinker.ShortLinker.service.UrlService;
import com.shortLinker.ShortLinker.utilty.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private UrlRepository repository;
    @Override
    public String shortenUrl(String longUrl) {
        UrlMapping entity = new UrlMapping();
        entity.setLongUrl(longUrl);
        entity.setCreatedAt(LocalDateTime.now());

        entity = repository.save(entity); // generates ID

        String shortKey = Base62Encoder.encode(entity.getId());
        entity.setShortKey(shortKey);

        repository.save(entity);
        return shortKey;
    }

    @Override
    public String getOriginalUrl(String shortKey){
        return repository.findByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("URL not found"))
                .getLongUrl();
    }
}
