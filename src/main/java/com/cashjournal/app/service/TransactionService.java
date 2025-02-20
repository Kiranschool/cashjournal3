package com.cashjournal.app.service;

import com.cashjournal.app.model.Transaction;
import com.cashjournal.app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public Transaction addExpense(String userId, BigDecimal amount, String purpose, 
                                LocalDateTime date, String reflection, String[] imageUrls) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount.negate()); // Store expenses as negative amounts
        transaction.setType("EXPENSE");
        transaction.setPurpose(purpose);
        transaction.setDate(date);
        transaction.setReflection(reflection);
        transaction.setImageUrls(imageUrls);
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction addIncome(String userId, BigDecimal amount, String source, LocalDateTime date) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setType("INCOME");
        transaction.setSource(source);
        transaction.setDate(date);
        
        return transactionRepository.save(transaction);
    }
    
    public BigDecimal calculateBalance(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<Transaction> getUserTransactions(String userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public Transaction updateTransaction(String id, Transaction updatedTransaction) {
        Transaction existingTransaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify the transaction belongs to the user
        if (!existingTransaction.getUserId().equals(updatedTransaction.getUserId())) {
            throw new RuntimeException("Unauthorized to update this transaction");
        }
        
        // Update the existing transaction
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setDate(updatedTransaction.getDate());
        existingTransaction.setType(updatedTransaction.getType());
        
        if (updatedTransaction.getType().equals("EXPENSE")) {
            existingTransaction.setPurpose(updatedTransaction.getPurpose());
            existingTransaction.setReflection(updatedTransaction.getReflection());
            existingTransaction.setSource(null);
        } else {
            existingTransaction.setSource(updatedTransaction.getSource());
            existingTransaction.setPurpose(null);
            existingTransaction.setReflection(null);
        }
        
        return transactionRepository.save(existingTransaction);
    }
    
    public void deleteTransaction(String id, String userId) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify the transaction belongs to the user
        if (!transaction.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this transaction");
        }
        
        transactionRepository.delete(transaction);
    }
} 