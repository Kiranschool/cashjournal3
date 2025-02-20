package com.cashjournal.app.controller;

import com.cashjournal.app.model.Transaction;
import com.cashjournal.app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/expense")
    public ResponseEntity<?> addExpense(@RequestBody Map<String, Object> request,
                                      Authentication authentication,
                                      HttpSession session) {
        try {
            String userId;
            if (authentication != null) {
                userId = authentication.getName();
            } else {
                userId = (String) session.getAttribute("username");
                if (userId == null) {
                    return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
                }
            }

            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String purpose = (String) request.get("purpose");
            String dateStr = (String) request.get("date");
            LocalDateTime date = LocalDateTime.parse(dateStr + "T00:00:00");
            String reflection = (String) request.getOrDefault("reflection", "");
            
            // Handle imageUrls as an array
            String[] imageUrls;
            if (request.containsKey("imageUrls")) {
                Object urlsObj = request.get("imageUrls");
                if (urlsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> urlsList = (List<String>) urlsObj;
                    imageUrls = urlsList.toArray(new String[0]);
                } else if (urlsObj instanceof String[]) {
                    imageUrls = (String[]) urlsObj;
                } else {
                    imageUrls = new String[0];
                }
            } else {
                imageUrls = new String[0];
            }
            
            Transaction transaction = transactionService.addExpense(
                userId, amount, purpose, date, reflection, imageUrls);
            
            return ResponseEntity.ok(Map.of(
                "message", "Expense added successfully",
                "transaction", transaction,
                "newBalance", transactionService.calculateBalance(userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/income")
    public ResponseEntity<?> addIncome(@RequestBody Map<String, Object> request,
                                     Authentication authentication,
                                     HttpSession session) {
        try {
            String userId;
            if (authentication != null) {
                userId = authentication.getName();
            } else {
                userId = (String) session.getAttribute("username");
                if (userId == null) {
                    return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
                }
            }

            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String source = (String) request.get("source");
            String dateStr = (String) request.get("date");
            LocalDateTime date = LocalDateTime.parse(dateStr + "T00:00:00");
            
            Transaction transaction = transactionService.addIncome(userId, amount, source, date);
            
            return ResponseEntity.ok(Map.of(
                "message", "Income added successfully",
                "transaction", transaction,
                "newBalance", transactionService.calculateBalance(userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication,
                                      HttpSession session) {
        try {
            String userId;
            if (authentication != null) {
                userId = authentication.getName();
            } else {
                userId = (String) session.getAttribute("username");
                if (userId == null) {
                    return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
                }
            }

            BigDecimal balance = transactionService.calculateBalance(userId);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getTransactions(Authentication authentication,
                                           HttpSession session) {
        try {
            String userId;
            if (authentication != null) {
                userId = authentication.getName();
            } else {
                userId = (String) session.getAttribute("username");
                if (userId == null) {
                    return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
                }
            }

            return ResponseEntity.ok(transactionService.getUserTransactions(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable String id,
                                             @RequestBody Map<String, Object> request,
                                             Authentication authentication,
                                             HttpSession session) {
        try {
            String userId;
            if (authentication != null) {
                userId = authentication.getName();
            } else {
                userId = (String) session.getAttribute("username");
                if (userId == null) {
                    return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
                }
            }

            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String dateStr = (String) request.get("date");
            LocalDateTime date = LocalDateTime.parse(dateStr + "T00:00:00");

            // Create updated transaction object
            Transaction updatedTransaction = new Transaction();
            updatedTransaction.setId(id);
            updatedTransaction.setUserId(userId);
            updatedTransaction.setDate(date);

            // Handle expense vs income specific fields
            if (request.containsKey("purpose")) {
                updatedTransaction.setType("EXPENSE");
                updatedTransaction.setAmount(amount.negate());
                updatedTransaction.setPurpose((String) request.get("purpose"));
                updatedTransaction.setReflection((String) request.get("reflection"));
            } else {
                updatedTransaction.setType("INCOME");
                updatedTransaction.setAmount(amount);
                updatedTransaction.setSource((String) request.get("source"));
            }

            Transaction transaction = transactionService.updateTransaction(id, updatedTransaction);
            
            return ResponseEntity.ok(Map.of(
                "message", "Transaction updated successfully",
                "transaction", transaction,
                "newBalance", transactionService.calculateBalance(userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String id,
                                             Authentication authentication,
                                             HttpSession session) {
        try {
            String userId;
            if (authentication != null) {
                userId = authentication.getName();
            } else {
                userId = (String) session.getAttribute("username");
                if (userId == null) {
                    return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
                }
            }

            transactionService.deleteTransaction(id, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Transaction deleted successfully",
                "newBalance", transactionService.calculateBalance(userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 