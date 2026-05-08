package com.ewallet.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single financial transaction.
 * Loaded from MySQL via DatabaseManager.
 */
public class Transaction {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String type;
    private double amount;
    private String timestamp;

    /** New in-memory transaction (timestamp = now). */
    public Transaction(String type, double amount) {
        this.type      = type;
        this.amount    = amount;
        this.timestamp = LocalDateTime.now().format(FMT);
    }

    /** Loaded from MySQL with stored timestamp. */
    public Transaction(String type, double amount, String timestamp) {
        this.type      = type;
        this.amount    = amount;
        this.timestamp = timestamp;
    }

    public String getType()      { return type; }
    public double getAmount()    { return amount; }
    public String getTimestamp() { return timestamp; }
}
