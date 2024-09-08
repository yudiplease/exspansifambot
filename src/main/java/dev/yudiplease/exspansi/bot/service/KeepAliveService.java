package dev.yudiplease.exspansi.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeepAliveService {
    private final Logger logger = LoggerFactory.getLogger(KeepAliveService.class);

    @Scheduled(fixedRate = 1 * 1000 * 60)
    public void reportCurrentTime() {
        logger.info("Бот не спит, работает");
    }
}
