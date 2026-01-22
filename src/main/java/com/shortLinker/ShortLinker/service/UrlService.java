package com.shortLinker.ShortLinker.service;

public interface UrlService {
    String shortenUrl(String longUrl);

    String getOriginalUrl(String shortKey);
}
