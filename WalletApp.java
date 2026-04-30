import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Main application entry point.
 * Builds the Swing GUI (Login -> Dashboard) and wires up all wallet actions.
 * Data is persisted to wallet_data.dat via Java Object Serialization.
 */
public class WalletApp extends JFrame {

    // Constants
    private static final String DATA_FILE = "wallet_data.dat";

    // Colour palette
    private static final Color BG_DARK     = new Color(18, 18, 30);
    private static final Color BG_CARD     = new Color(30, 30, 50);
    private static final Color ACCENT      = new Color(99, 102, 241);
    private static final Color ACCENT_DARK = new Color(67, 56, 202);
    private static final Color SUCCESS     = new Color(34, 197, 94);
    private static final Color DANGER      = new Color(239, 68, 68);
    private static final Color TEXT_LIGHT  = new Color(226, 232, 240);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);

    // State
    private ArrayList<User> users;
    private User currentUser;

    // UI Components
    private JPanel     mainPanel;
    private CardLayout cardLayout;

    private JTextField     tfUser;
    private JPasswordField pfPass;

    private JLabel    lblWelcome;
    private JLabel    lblBalance;
    private JTextArea txtHistory;

    // =========================================================================
    public WalletApp() {
        loadData();
        setupFrame();
        buildLoginPanel();
        buildDashboardPanel();
        setVisible(true);
    }

    // Frame Setup
    private void setupFrame() {
        setTitle("Java E-Wallet System");
        setSize(520, 600);
        setMinimumSize(new Dimension(420, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(BG_DARK);
        add(mainPanel);
    }

    // =========================================================================
    // LOGIN PANEL
    // =========================================================================
    private void buildLoginPanel() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));

        JLabel title = new JLabel("E-Wallet Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Access your digital wallet", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        tfUser = new JTextField();
        styleField(tfUser);

        pfPass = new JPasswordField();
        styleField(pfPass);

        JButton btnLogin    = createPrimaryButton("Login", ACCENT, ACCENT_DARK);
        JButton btnRegister = createLinkButton("New here? Register an account");

        btnLogin.addActionListener(e -> doLogin());
        tfUser.addActionListener(e -> doLogin());
        pfPass.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> doRegister());

        card.add(title);
        card.add(vGap(4));
        card.add(subtitle);
        card.add(vGap(24));
        card.add(fieldLabel("Username"));
        card.add(vGap(4));
        card.add(tfUser);
        card.add(vGap(12));
        card.add(fieldLabel("Password"));
        card.add(vGap(4));
        card.add(pfPass);
        card.add(vGap(20));
        card.add(btnLogin);
        card.add(vGap(10));
        card.add(btnRegister);

        root.add(card);
        mainPanel.add(root, "LOGIN");
    }

    private void doLogin() {
        String uname = tfUser.getText().trim();
        String pass  = new String(pfPass.getPassword());
        for (User u : users) {
            if (u.getUsername().equals(uname) && u.getPassword().equals(pass)) {
                currentUser = u;
                refreshDashboard();
                cardLayout.show(mainPanel, "DASHBOARD");
                pfPass.setText("");
                return;
            }
        }
        showError("Invalid username or password.");
    }

    private void doRegister() {
        String uname = JOptionPane.showInputDialog(this,
                "Choose a username:", "Register", JOptionPane.PLAIN_MESSAGE);
        if (uname == null || uname.isBlank()) return;

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(uname)) {
                showError("Username already exists!");
                return;
            }
        }

        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, pf,
                "Choose a password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String pass = new String(pf.getPassword());
        if (pass.isBlank()) { showError("Password cannot be empty."); return; }

        users.add(new User(uname, pass, 0.0));
        saveData();
        showInfo("Account created! You can now log in.", "Registration Successful");
    }

    // =========================================================================
    // DASHBOARD PANEL
    // =========================================================================
    private void buildDashboardPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        lblWelcome = new JLabel("Welcome");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWelcome.setForeground(TEXT_MUTED);

        lblBalance = new JLabel("$0.00");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblBalance.setForeground(SUCCESS);

        JLabel balLabel = new JLabel("Available Balance");
        balLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        balLabel.setForeground(TEXT_MUTED);

        JButton btnLogout = createLinkButton("Logout");
        btnLogout.addActionListener(e -> {
            currentUser = null;
            tfUser.setText("");
            cardLayout.show(mainPanel, "LOGIN");
        });

        JPanel headerLeft = new JPanel();
        headerLeft.setBackground(BG_CARD);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.add(lblWelcome);
        headerLeft.add(vGap(6));
        headerLeft.add(lblBalance);
        headerLeft.add(balLabel);

        header.add(headerLeft, BorderLayout.WEST);
        header.add(btnLogout,  BorderLayout.EAST);

        // Action Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 3, 10, 0));
        btnRow.setBackground(BG_DARK);
        btnRow.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));

        JButton btnAdd      = createPrimaryButton("Add Money", SUCCESS, new Color(22, 163, 74));
        JButton btnPay      = createPrimaryButton("Pay",       DANGER,  new Color(185, 28, 28));
        JButton btnTransfer = createPrimaryButton("Transfer",  ACCENT,  ACCENT_DARK);

        btnAdd.addActionListener(e -> doAdd());
        btnPay.addActionListener(e -> doPay());
        btnTransfer.addActionListener(e -> doTransfer());

        btnRow.add(btnAdd);
        btnRow.add(btnPay);
        btnRow.add(btnTransfer);

        // Transaction History
        JLabel histTitle = new JLabel("  Transaction History");
        histTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        histTitle.setForeground(TEXT_LIGHT);
        histTitle.setOpaque(true);
        histTitle.setBackground(BG_DARK);
        histTitle.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 0));

        txtHistory = new JTextArea();
        txtHistory.setEditable(false);
        txtHistory.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtHistory.setBackground(BG_CARD);
        txtHistory.setForeground(TEXT_LIGHT);
        txtHistory.setCaretColor(TEXT_LIGHT);
        txtHistory.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(txtHistory);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_CARD);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(BG_DARK);
        centre.add(histTitle, BorderLayout.NORTH);
        centre.add(scroll,    BorderLayout.CENTER);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BG_DARK);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(btnRow, BorderLayout.SOUTH);

        root.add(topSection, BorderLayout.NORTH);
        root.add(centre,     BorderLayout.CENTER);

        mainPanel.add(root, "DASHBOARD");
    }

    // Wallet Actions
    private void doAdd() {
        String input = JOptionPane.showInputDialog(this,
                "Enter amount to deposit:", "Add Money", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;
        try {
            double amount = Double.parseDouble(input.trim());
            if (amount <= 0) { showError("Amount must be positive."); return; }
            currentUser.addAmount(amount);
            saveData();
            refreshDashboard();
            showInfo(String.format("$%.2f added to your wallet.", amount), "Deposit Successful");
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        }
    }

    private void doPay() {
        String input = JOptionPane.showInputDialog(this,
                "Enter payment amount:", "Make Payment", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;
        try {
            double amount = Double.parseDouble(input.trim());
            if (amount <= 0) { showError("Amount must be positive."); return; }
            if (currentUser.pay(amount)) {
                saveData();
                refreshDashboard();
                showInfo(String.format("Payment of $%.2f processed.", amount), "Payment Successful");
            } else {
                showError("Insufficient balance!");
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        }
    }

    private void doTransfer() {
        String recipientName = JOptionPane.showInputDialog(this,
                "Enter recipient username:", "Transfer Funds", JOptionPane.PLAIN_MESSAGE);
        if (recipientName == null || recipientName.isBlank()) return;

        if (recipientName.equalsIgnoreCase(currentUser.getUsername())) {
            showError("You cannot transfer to yourself.");
            return;
        }

        User recipient = null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(recipientName)) { recipient = u; break; }
        }
        if (recipient == null) {
            showError("User \"" + recipientName + "\" not found.");
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Enter amount to transfer to " + recipient.getUsername() + ":",
                "Transfer Funds", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        try {
            double amount = Double.parseDouble(input.trim());
            if (amount <= 0) { showError("Amount must be positive."); return; }
            if (currentUser.transferTo(recipient, amount)) {
                saveData();
                refreshDashboard();
                showInfo(String.format("$%.2f transferred to %s.", amount, recipient.getUsername()),
                        "Transfer Successful");
            } else {
                showError("Transfer failed. Insufficient balance.");
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        }
    }

    // Dashboard Refresh
    private void refreshDashboard() {
        lblWelcome.setText("Welcome, " + currentUser.getUsername());
        lblBalance.setText(String.format("$%.2f", currentUser.getCurrentBalance()));

        txtHistory.setText("");
        var history = currentUser.getHistory();
        if (history.isEmpty()) {
            txtHistory.setText("  No transactions yet.\n");
        } else {
            // Newest-first order
            for (int i = history.size() - 1; i >= 0; i--) {
                txtHistory.append("  " + history.get(i).toString() + "\n");
            }
        }
        txtHistory.setCaretPosition(0);
    }

    // Persistence
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (ArrayList<User>) ois.readObject();
            System.out.println("Loaded data from " + DATA_FILE);
        } catch (Exception e) {
            users = new ArrayList<>();
            users.add(new User("Ranjeet_Gandolli", "pass123", 1000.00));
            users.add(new User("User2",             "pass456",  500.00));
            System.out.println("No save file found. Default test accounts created.");
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            showError("Could not save data: " + e.getMessage());
        }
    }

    // UI Helpers
    private void styleField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 90), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton createPrimaryButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg);    }
        });
        return btn;
    }

    private JButton createLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(ACCENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private Component vGap(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WalletApp::new);
    }
}
