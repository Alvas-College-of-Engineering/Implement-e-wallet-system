package com.ewallet.dao;

import com.ewallet.model.Transaction;
import com.ewallet.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * All MySQL JDBC operations for the E-Wallet web application.
 * Each method obtains its own connection (no shared singleton)
 * to be safe in a multi-threaded Servlet environment.
 */
public class DatabaseManager {

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "ewallet_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123";   // set during reset

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/"
            + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // ── Bootstrap ─────────────────────────────────────────────────────────────

    /**
     * Called once at application startup (from AppContextListener).
     * Ensures the database and tables exist, and seeds demo accounts.
     */
    public static void initialize() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }

        // Create DB if not exists
        String baseUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT
                + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection c = DriverManager.getConnection(baseUrl, DB_USER, DB_PASS);
             Statement  s = c.createStatement()) {
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }

        createTables();
        seedDemoAccounts();
        System.out.println("[DB] Initialized → " + DB_NAME);
    }

    /** Open a fresh connection to ewallet_db. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // ── Schema ────────────────────────────────────────────────────────────────

    private static void createTables() throws SQLException {
        String users = """
                CREATE TABLE IF NOT EXISTS users (
                    id       INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50)  NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    balance  DOUBLE       NOT NULL DEFAULT 0.0
                )""";
        String txns = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id         INT AUTO_INCREMENT PRIMARY KEY,
                    user_id    INT          NOT NULL,
                    type       VARCHAR(100) NOT NULL,
                    amount     DOUBLE       NOT NULL,
                    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )""";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate(users);
            s.executeUpdate(txns);
        }
    }

    private static void seedDemoAccounts() throws SQLException {
        try (Connection c = getConnection();
             Statement  s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                createUser("Ranjeet_Gandolli", "pass123", 1000.00);
                createUser("User2",             "pass456",  500.00);
                System.out.println("[DB] Demo accounts seeded.");
            }
        }
    }

    // ── User CRUD ─────────────────────────────────────────────────────────────

    /**
     * Returns the User if username+password match, otherwise null.
     */
    public static User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, balance FROM users WHERE username = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("password").equals(password)) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                                    rs.getString("password"), rs.getDouble("balance"));
                }
            }
        }
        return null;
    }

    public static boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public static User createUser(String username, String password, double balance)
            throws SQLException {
        String sql = "INSERT INTO users (username, password, balance) VALUES (?,?,?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setDouble(3, balance);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new User(keys.getInt(1), username, password, balance);
            }
        }
        throw new SQLException("User creation failed.");
    }

    /** Refreshes a user's balance from the DB and returns the updated object. */
    public static User refreshUser(int userId) throws SQLException {
        String sql = "SELECT id, username, password, balance FROM users WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                                    rs.getString("password"), rs.getDouble("balance"));
                }
            }
        }
        throw new SQLException("User not found: id=" + userId);
    }

    private static void updateBalance(Connection c, int userId, double balance) throws SQLException {
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, balance);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // ── Transaction CRUD ──────────────────────────────────────────────────────

    public static List<Transaction> getTransactions(int userId) throws SQLException {
        String sql = "SELECT type, amount, created_at FROM transactions " +
                     "WHERE user_id = ? ORDER BY created_at DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(rs.getString("type"),
                                             rs.getDouble("amount"),
                                             rs.getString("created_at")));
                }
            }
        }
        return list;
    }

    private static void insertTransaction(Connection c, int userId, String type, double amount)
            throws SQLException {
        String sql = "INSERT INTO transactions (user_id, type, amount) VALUES (?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }
    }

    // ── Wallet Operations (Atomic) ────────────────────────────────────────────

    /** Deposit: adds amount to user balance. */
    public static void deposit(int userId, double amount) throws SQLException {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                double newBal = getCurrentBalance(c, userId) + amount;
                updateBalance(c, userId, newBal);
                insertTransaction(c, userId, "DEPOSIT", amount);
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    /** Payment: deducts amount from user balance. Returns false if insufficient. */
    public static boolean pay(int userId, double amount) throws SQLException {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                double bal = getCurrentBalance(c, userId);
                if (bal < amount) { c.rollback(); return false; }
                updateBalance(c, userId, bal - amount);
                insertTransaction(c, userId, "PAYMENT", amount);
                c.commit();
                return true;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    /**
     * Atomic transfer between two users.
     * Both balance updates and both transaction rows committed together.
     */
    public static boolean transfer(int senderId, int recipientId, double amount)
            throws SQLException {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                double senderBal = getCurrentBalance(c, senderId);
                if (senderBal < amount) { c.rollback(); return false; }

                String senderName    = getUsername(c, senderId);
                String recipientName = getUsername(c, recipientId);

                updateBalance(c, senderId,    senderBal - amount);
                updateBalance(c, recipientId, getCurrentBalance(c, recipientId) + amount);
                insertTransaction(c, senderId,    "TRANSFER TO " + recipientName,   amount);
                insertTransaction(c, recipientId, "RECEIVED FROM " + senderName,    amount);
                c.commit();
                return true;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private static double getCurrentBalance(Connection c, int userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT balance FROM users WHERE id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
            }
        }
        throw new SQLException("User not found: id=" + userId);
    }

    private static String getUsername(Connection c, int userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT username FROM users WHERE id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("username");
            }
        }
        throw new SQLException("User not found: id=" + userId);
    }

    public static User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, balance FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                                    rs.getString("password"), rs.getDouble("balance"));
                }
            }
        }
        return null;
    }
}
