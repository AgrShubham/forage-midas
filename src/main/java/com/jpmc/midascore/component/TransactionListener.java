package com.jpmc.midascore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.foundation.Transaction;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final DatabaseConduit databaseConduit;

    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @KafkaListener(topics = "${general.kafka-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void onTransaction(@Payload Transaction transaction) {
        // Validate and record if valid
        boolean recorded = databaseConduit.validateAndRecord(
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount()
        );
        if (recorded) {
            logger.info("Recorded transaction: {}", transaction);
        } else {
            logger.info("Discarded invalid transaction: {}", transaction);
        }
    }
}


