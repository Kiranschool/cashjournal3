package com.cashjournal.app.repository;

import com.cashjournal.app.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByUserId(String userId);
    
    @Query("{ 'userId': ?0 }")
    List<Transaction> findAllUserTransactions(String userId);
    
    @Query(value = "{ 'userId': ?0 }", fields = "{ 'amount': 1 }")
    List<Transaction> findAllUserTransactionAmounts(String userId);
} 