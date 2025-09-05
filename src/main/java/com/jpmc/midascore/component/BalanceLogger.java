package com.jpmc.midascore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.repository.UserRepository;

@Component
public class BalanceLogger {
    private static final Logger logger = LoggerFactory.getLogger(BalanceLogger.class);
    private final UserRepository userRepository;

    public BalanceLogger(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedDelay = 2000)
    public void logWilburBalance() {
        try {
            var user = userRepository.findById(9L);
            if (user != null) {
                logger.info("WILBUR_BALANCE={} (roundedDown={})", user.getBalance(), (int) Math.floor(user.getBalance()));
            }
        } catch (Exception ignored) {}
    }
}


