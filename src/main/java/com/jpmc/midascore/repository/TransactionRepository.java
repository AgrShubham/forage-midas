package com.jpmc.midascore.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpmc.midascore.entity.TransactionRecord;

public interface TransactionRepository extends CrudRepository<TransactionRecord, Long> {
}


