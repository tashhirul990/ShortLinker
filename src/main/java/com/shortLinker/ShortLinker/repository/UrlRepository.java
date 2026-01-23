package com.shortLinker.ShortLinker.repository;

import com.shortLinker.ShortLinker.entity.UrlMapping;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByLongUrl(String shortKey);

    Optional<UrlMapping> findByShortKey(String longUrl);

    @Modifying
    @Transactional
    @Query("DELETE FROM UrlMapping u WHERE u.createdAt < :pastTime")
    int deleteExpiredUrls(@Param("pastTime") OffsetDateTime pastTime);

}
