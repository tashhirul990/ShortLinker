package com.shortLinker.ShortLinker.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RedisValues {
    private String longUrl;
    private OffsetDateTime createdAt;
}
