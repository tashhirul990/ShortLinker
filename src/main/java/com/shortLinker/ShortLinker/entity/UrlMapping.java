package com.shortLinker.ShortLinker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "url_mapping")
@Setter
@Getter
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String shortKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    private OffsetDateTime createdAt;
}


