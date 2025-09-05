package com.jpmc.midascore.component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveClient incentiveClient;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveClient incentiveClient) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveClient = incentiveClient;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    @Transactional
    public boolean validateAndRecord(long senderId, long recipientId, float amount) {
        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);
        if (sender == null || recipient == null) {
            return false;
        }
        if (sender.getBalance() < amount) {
            return false;
        }
        // Fetch incentive from external API and apply only to recipient
        float incentive = 0.0f;
        try {
            incentive = incentiveClient.fetchIncentiveAmount(new com.jpmc.midascore.foundation.Transaction(senderId, recipientId, amount));
            if (incentive < 0) {
                incentive = 0.0f;
            }
        } catch (Exception e) {
            incentive = 0.0f;
        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);
        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(new TransactionRecord(sender, recipient, amount, incentive));
        return true;
    }
}
