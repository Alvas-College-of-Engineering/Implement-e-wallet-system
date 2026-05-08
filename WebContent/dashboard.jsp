<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ewallet.model.User, com.ewallet.model.Transaction, java.util.List" %>
<%
    // Auth guard
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<Transaction> txns = (List<Transaction>) request.getAttribute("transactions");

    // Flash messages
    String flashSuccess = (String) session.getAttribute("flash_success");
    String flashError   = (String) session.getAttribute("flash_error");
    session.removeAttribute("flash_success");
    session.removeAttribute("flash_error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Wallet — Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --bg:       #0f0f1a;
            --surface:  #1a1a2e;
            --card:     #16213e;
            --card2:    #1e1e3a;
            --accent:   #6366f1;
            --accent2:  #818cf8;
            --success:  #22c55e;
            --danger:   #ef4444;
            --warning:  #f59e0b;
            --text:     #e2e8f0;
            --muted:    #64748b;
            --border:   #2d2d4e;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--bg);
            color: var(--text);
            min-height: 100vh;
            background-image:
                radial-gradient(ellipse at 10% 10%, rgba(99,102,241,0.12) 0%, transparent 50%),
                radial-gradient(ellipse at 90% 90%, rgba(129,140,248,0.08) 0%, transparent 50%);
        }

        /* ── Navbar ── */
        .navbar {
            background: rgba(22,33,62,0.95);
            backdrop-filter: blur(12px);
            border-bottom: 1px solid var(--border);
            padding: 0 32px;
            height: 64px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .navbar-brand {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 1.1rem;
            font-weight: 700;
            color: var(--text);
        }
        .navbar-brand span { font-size: 1.4rem; }
        .navbar-user {
            display: flex;
            align-items: center;
            gap: 14px;
        }
        .avatar {
            width: 36px; height: 36px;
            background: linear-gradient(135deg, var(--accent), #4f46e5);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 0.85rem;
        }
        .username-text { font-size: 0.9rem; font-weight: 500; color: var(--text); }
        .btn-logout {
            padding: 7px 16px;
            background: transparent;
            border: 1px solid var(--border);
            border-radius: 8px;
            color: var(--muted);
            font-family: 'Inter', sans-serif;
            font-size: 0.82rem;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s;
        }
        .btn-logout:hover { border-color: var(--danger); color: var(--danger); }

        /* ── Layout ── */
        .container {
            max-width: 1100px;
            margin: 0 auto;
            padding: 32px 24px;
        }

        /* ── Flash messages ── */
        .flash {
            padding: 14px 18px;
            border-radius: 12px;
            font-size: 0.9rem;
            font-weight: 500;
            margin-bottom: 24px;
            display: flex;
            align-items: center;
            gap: 10px;
            animation: slideDown 0.3s ease;
        }
        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-10px); }
            to   { opacity: 1; transform: translateY(0); }
        }
        .flash-success { background: rgba(34,197,94,0.12); border: 1px solid rgba(34,197,94,0.3); color: #86efac; }
        .flash-error   { background: rgba(239,68,68,0.12); border: 1px solid rgba(239,68,68,0.3); color: #fca5a5; }

        /* ── Balance Card ── */
        .balance-card {
            background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 60%, #6366f1 100%);
            border-radius: 20px;
            padding: 32px 36px;
            margin-bottom: 28px;
            position: relative;
            overflow: hidden;
            box-shadow: 0 10px 40px rgba(99,102,241,0.35);
        }
        .balance-card::before {
            content: '';
            position: absolute;
            top: -40px; right: -40px;
            width: 180px; height: 180px;
            background: rgba(255,255,255,0.08);
            border-radius: 50%;
        }
        .balance-card::after {
            content: '';
            position: absolute;
            bottom: -60px; left: 30%;
            width: 240px; height: 240px;
            background: rgba(255,255,255,0.05);
            border-radius: 50%;
        }
        .balance-label { font-size: 0.85rem; color: rgba(255,255,255,0.7); margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.08em; }
        .balance-amount {
            font-size: 3rem;
            font-weight: 800;
            color: #fff;
            letter-spacing: -0.02em;
            line-height: 1;
            margin-bottom: 8px;
        }
        .balance-user { font-size: 0.9rem; color: rgba(255,255,255,0.75); }
        .balance-badge {
            position: absolute;
            top: 28px; right: 36px;
            background: rgba(255,255,255,0.15);
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 20px;
            padding: 6px 14px;
            font-size: 0.8rem;
            color: #fff;
            font-weight: 600;
        }

        /* ── Action Cards Grid ── */
        .actions-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
            margin-bottom: 28px;
        }
        .action-card {
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 20px;
            transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
        }
        .action-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 30px rgba(0,0,0,0.3);
        }
        .action-card.deposit:hover { border-color: var(--success); }
        .action-card.pay:hover     { border-color: var(--danger);  }
        .action-card.transfer:hover{ border-color: var(--accent);  }

        .action-icon {
            width: 44px; height: 44px;
            border-radius: 12px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.2rem;
            margin-bottom: 12px;
        }
        .icon-green  { background: rgba(34,197,94,0.15); }
        .icon-red    { background: rgba(239,68,68,0.15); }
        .icon-indigo { background: rgba(99,102,241,0.15); }

        .action-title { font-size: 0.95rem; font-weight: 600; color: var(--text); margin-bottom: 4px; }
        .action-desc  { font-size: 0.8rem; color: var(--muted); margin-bottom: 14px; }

        .action-form { display: flex; flex-direction: column; gap: 8px; }
        .action-form input[type="text"],
        .action-form input[type="number"] {
            background: var(--surface);
            border: 1.5px solid var(--border);
            border-radius: 9px;
            padding: 9px 12px;
            color: var(--text);
            font-family: 'Inter', sans-serif;
            font-size: 0.875rem;
            outline: none;
            transition: border-color 0.2s;
            width: 100%;
        }
        .action-form input:focus { border-color: var(--accent); }
        .action-form input::placeholder { color: var(--muted); }

        .btn-action {
            padding: 9px 14px;
            border: none;
            border-radius: 9px;
            font-family: 'Inter', sans-serif;
            font-size: 0.85rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            width: 100%;
        }
        .btn-green  { background: var(--success); color: #fff; }
        .btn-green:hover  { background: #16a34a; box-shadow: 0 4px 12px rgba(34,197,94,0.35); }
        .btn-red    { background: var(--danger);  color: #fff; }
        .btn-red:hover    { background: #dc2626; box-shadow: 0 4px 12px rgba(239,68,68,0.35); }
        .btn-indigo { background: var(--accent);  color: #fff; }
        .btn-indigo:hover { background: #4f46e5; box-shadow: 0 4px 12px rgba(99,102,241,0.35); }

        /* ── Transaction History ── */
        .section-title {
            font-size: 1rem;
            font-weight: 700;
            color: var(--text);
            margin-bottom: 14px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .section-title::after {
            content: '';
            flex: 1;
            height: 1px;
            background: var(--border);
        }

        .tx-table {
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 16px;
            overflow: hidden;
            width: 100%;
        }
        .tx-table table {
            width: 100%;
            border-collapse: collapse;
        }
        .tx-table th {
            background: var(--surface);
            padding: 12px 20px;
            font-size: 0.75rem;
            font-weight: 600;
            color: var(--muted);
            text-transform: uppercase;
            letter-spacing: 0.06em;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }
        .tx-table td {
            padding: 14px 20px;
            font-size: 0.875rem;
            color: var(--text);
            border-bottom: 1px solid rgba(45,45,78,0.5);
        }
        .tx-table tr:last-child td { border-bottom: none; }
        .tx-table tr:hover td { background: rgba(99,102,241,0.05); }

        .tx-type { font-weight: 600; }
        .tx-deposit  { color: var(--success); }
        .tx-payment  { color: var(--danger); }
        .tx-transfer { color: var(--accent2); }
        .tx-received { color: var(--success); }

        .tx-amount { font-weight: 700; font-variant-numeric: tabular-nums; }
        .tx-amount.out { color: var(--danger); }
        .tx-amount.in  { color: var(--success); }

        .tx-timestamp { color: var(--muted); font-size: 0.8rem; }

        .empty-state {
            padding: 40px;
            text-align: center;
            color: var(--muted);
        }
        .empty-state .icon { font-size: 2.5rem; margin-bottom: 10px; }

        /* ── Stats row ── */
        .stats-row {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
            margin-bottom: 28px;
        }
        .stat-card {
            background: var(--card2);
            border: 1px solid var(--border);
            border-radius: 14px;
            padding: 16px 20px;
            display: flex;
            align-items: center;
            gap: 14px;
        }
        .stat-icon { font-size: 1.4rem; }
        .stat-val  { font-size: 1.2rem; font-weight: 700; color: var(--text); }
        .stat-lbl  { font-size: 0.78rem; color: var(--muted); }

        @media (max-width: 768px) {
            .actions-grid { grid-template-columns: 1fr; }
            .stats-row    { grid-template-columns: 1fr; }
            .balance-amount { font-size: 2.2rem; }
        }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar">
    <div class="navbar-brand">
        <span>💳</span> E-Wallet
    </div>
    <div class="navbar-user">
        <div class="avatar"><%= user.getUsername().substring(0,1).toUpperCase() %></div>
        <span class="username-text"><%= user.getUsername() %></span>
        <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Logout</a>
    </div>
</nav>

<div class="container">

    <!-- Flash Messages -->
    <% if (flashSuccess != null) { %>
        <div class="flash flash-success">✓ <%= flashSuccess %></div>
    <% } %>
    <% if (flashError != null) { %>
        <div class="flash flash-error">⚠ <%= flashError %></div>
    <% } %>

    <!-- Balance Card -->
    <div class="balance-card">
        <div class="balance-badge">💳 Active</div>
        <div class="balance-label">Available Balance</div>
        <div class="balance-amount">₹<%= String.format("%,.2f", user.getBalance()) %></div>
        <div class="balance-user">Welcome, <%= user.getUsername() %></div>
    </div>

    <!-- Stats Row -->
    <%
        int totalTxns   = (txns != null) ? txns.size() : 0;
        int deposits    = 0; int payments = 0;
        double totalIn  = 0; double totalOut = 0;
        if (txns != null) {
            for (Transaction t : txns) {
                if (t.getType().startsWith("DEPOSIT") || t.getType().startsWith("RECEIVED")) {
                    deposits++; totalIn += t.getAmount();
                } else {
                    payments++; totalOut += t.getAmount();
                }
            }
        }
    %>
    <div class="stats-row">
        <div class="stat-card">
            <div class="stat-icon">📊</div>
            <div>
                <div class="stat-val"><%= totalTxns %></div>
                <div class="stat-lbl">Total Transactions</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">📥</div>
            <div>
                <div class="stat-val" style="color:var(--success)">₹<%= String.format("%,.2f", totalIn) %></div>
                <div class="stat-lbl">Total Received</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">📤</div>
            <div>
                <div class="stat-val" style="color:var(--danger)">₹<%= String.format("%,.2f", totalOut) %></div>
                <div class="stat-lbl">Total Sent</div>
            </div>
        </div>
    </div>

    <!-- Action Cards -->
    <div class="actions-grid">

        <!-- Deposit -->
        <div class="action-card deposit">
            <div class="action-icon icon-green">💰</div>
            <div class="action-title">Add Money</div>
            <div class="action-desc">Deposit funds into your wallet</div>
            <form class="action-form" action="<%= request.getContextPath() %>/deposit" method="post">
                <input type="number" name="amount" placeholder="Enter amount (₹)" min="1" step="0.01" required>
                <button type="submit" class="btn-action btn-green">Deposit →</button>
            </form>
        </div>

        <!-- Pay -->
        <div class="action-card pay">
            <div class="action-icon icon-red">🧾</div>
            <div class="action-title">Make Payment</div>
            <div class="action-desc">Pay for goods &amp; services</div>
            <form class="action-form" action="<%= request.getContextPath() %>/pay" method="post">
                <input type="number" name="amount" placeholder="Enter amount (₹)" min="1" step="0.01" required>
                <button type="submit" class="btn-action btn-red">Pay Now →</button>
            </form>
        </div>

        <!-- Transfer -->
        <div class="action-card transfer">
            <div class="action-icon icon-indigo">🔄</div>
            <div class="action-title">Transfer Funds</div>
            <div class="action-desc">Send money to another user</div>
            <form class="action-form" action="<%= request.getContextPath() %>/transfer" method="post">
                <input type="text" name="recipient" placeholder="Recipient username" required>
                <input type="number" name="amount"    placeholder="Enter amount (₹)" min="1" step="0.01" required>
                <button type="submit" class="btn-action btn-indigo">Transfer →</button>
            </form>
        </div>

    </div>

    <!-- Transaction History -->
    <div class="section-title">📋 Transaction History</div>
    <div class="tx-table">
        <% if (txns == null || txns.isEmpty()) { %>
            <div class="empty-state">
                <div class="icon">🗂️</div>
                <div>No transactions yet. Make your first deposit!</div>
            </div>
        <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Date &amp; Time</th>
                </tr>
            </thead>
            <tbody>
                <% int idx = 1; for (Transaction t : txns) {
                    boolean isIn = t.getType().startsWith("DEPOSIT") || t.getType().startsWith("RECEIVED");
                    String typeClass = t.getType().startsWith("DEPOSIT") ? "tx-deposit"
                                     : t.getType().startsWith("PAYMENT") ? "tx-payment"
                                     : t.getType().startsWith("TRANSFER") ? "tx-transfer"
                                     : "tx-received";
                %>
                <tr>
                    <td style="color:var(--muted)"><%= idx++ %></td>
                    <td><span class="tx-type <%= typeClass %>"><%= t.getType() %></span></td>
                    <td><span class="tx-amount <%= isIn ? "in" : "out" %>">
                        <%= isIn ? "+" : "-" %>₹<%= String.format("%,.2f", t.getAmount()) %>
                    </span></td>
                    <td><span class="tx-timestamp"><%= t.getTimestamp() %></span></td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } %>
    </div>

</div>

</body>
</html>
