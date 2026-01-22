package com.shortLinker.ShortLinker.scheduler;

import com.shortLinker.ShortLinker.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@Slf4j
public class ExpiredUrlCleanupJob {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ExpiredUrlCleanupJob.class);

    private final UrlRepository repository;

    @Value("${app.expriration-days}")
    private Integer exparationPeriod;

    public ExpiredUrlCleanupJob(UrlRepository repository) {
        this.repository = repository;
    }


    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
    public void cleanExpiredUrls() {

        LOGGER.info("🔄 Expired URL cleanup job started");
        LOGGER.info("Expiration period is set to: {}", exparationPeriod);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expirationThreshold = now.minus(Duration.ofDays(exparationPeriod));
        int deletedCount = repository.deleteExpiredUrls(expirationThreshold);

        LOGGER.info("✅ Expired URL cleanup completed. Deleted {} records", deletedCount);
    }
}
