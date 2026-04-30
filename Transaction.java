import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single financial transaction in the wallet.
 * Implements Serializable so it can be saved to a .dat file.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private double amount;
    private String timestamp;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getType()      { return type; }
    public double getAmount()    { return amount; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s]  %-26s  $%.2f", timestamp, type, amount);
    }
}
