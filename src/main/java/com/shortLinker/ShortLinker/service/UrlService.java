package com.shortLinker.ShortLinker.service;

public interface UrlService {
    public String shortenUrl(String longUrl);
    public String getOriginalUrl(String shortKey);
}
