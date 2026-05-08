package com.ewallet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an E-Wallet user.
 * Holds credentials, balance, and in-memory transaction history.
 */
public class User {

    private int    id;
    private String username;
    private String password;
    private double balance;
    private List<Transaction> history;

    public User(int id, String username, String password, double balance) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.balance  = balance;
        this.history  = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int               getId()       { return id; }
    public String            getUsername() { return username; }
    public String            getPassword() { return password; }
    public double            getBalance()  { return balance; }
    public List<Transaction> getHistory()  { return history; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setBalance(double balance)           { this.balance = balance; }
    public void setHistory(List<Transaction> history){ this.history = history; }
}
