package com.shortLinker.ShortLinker.controller;

import com.shortLinker.ShortLinker.DTO.ShortenUrlRequest;
import com.shortLinker.ShortLinker.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@Slf4j
public class UrlController {

    @Autowired
    private UrlService service;

    private final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping(value = "/shorten")
    public String shorten(@RequestBody ShortenUrlRequest longUrl) {
        LOGGER.info("Long URL received for shortening: {}", longUrl.getLongUrl());
        return baseUrl+"/api/"+service.shortenUrl(longUrl.getLongUrl());
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {
        String longUrl = service.getOriginalUrl(shortKey);
        if(longUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        LOGGER.info("Redirecting short key {} to long URL {}", shortKey, longUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}
