package com.cashjournal.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String userId;
    private BigDecimal amount;
    private String type; // "EXPENSE" or "INCOME"
    private String purpose;
    private LocalDateTime date;
    private String reflection;
    private String[] imageUrls;
    private String source; // For income transactions
} 