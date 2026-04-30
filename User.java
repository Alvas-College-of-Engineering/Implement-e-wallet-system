import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores user credentials, current balance, and the full transaction history.
 * All wallet operations (add, pay, transfer) live here and automatically
 * log a Transaction entry on every successful action.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 2L;

    private String username;
    private String password;
    private double currentBalance;
    private List<Transaction> history;

    public User(String username, String password, double initialBalance) {
        this.username       = username;
        this.password       = password;
        this.currentBalance = initialBalance;
        this.history        = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String           getUsername()       { return username; }
    public String           getPassword()       { return password; }
    public double           getCurrentBalance() { return currentBalance; }
    public List<Transaction> getHistory()       { return history; }

    // ── Wallet Operations ──────────────────────────────────────────────────

    /**
     * Deposits a positive amount into this wallet.
     */
    public void addAmount(double amount) {
        if (amount <= 0) return;
        currentBalance += amount;
        history.add(new Transaction("DEPOSIT", amount));
    }

    /**
     * Deducts amount for a payment. Returns false if funds are insufficient.
     */
    public boolean pay(double amount) {
        if (amount <= 0 || currentBalance < amount) return false;
        currentBalance -= amount;
        history.add(new Transaction("PAYMENT", amount));
        return true;
    }

    /**
     * Moves funds from this user to the recipient.
     * Both sides get a history entry.
     * Returns false if the amount is invalid, balance is short, or recipient is null.
     */
    public boolean transferTo(User recipient, double amount) {
        if (amount <= 0 || currentBalance < amount || recipient == null) return false;
        this.currentBalance      -= amount;
        recipient.currentBalance += amount;
        this.history.add(new Transaction("TRANSFER TO " + recipient.getUsername(), amount));
        recipient.history.add(new Transaction("RECEIVED FROM " + this.username, amount));
        return true;
    }
}
