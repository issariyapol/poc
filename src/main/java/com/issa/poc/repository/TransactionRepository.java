package com.issa.poc.repository;

import com.issa.poc.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdAndTransactionDateTimeBetweenOrderByTransactionDateTime(Long accountId, LocalDateTime from, LocalDateTime to);
}
