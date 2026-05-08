<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.lang.String" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Wallet — Login</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --bg:       #0f0f1a;
            --surface:  #1a1a2e;
            --card:     #16213e;
            --accent:   #6366f1;
            --accent2:  #818cf8;
            --success:  #22c55e;
            --danger:   #ef4444;
            --text:     #e2e8f0;
            --muted:    #64748b;
            --border:   #2d2d4e;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--bg);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-image:
                radial-gradient(ellipse at 20% 20%, rgba(99,102,241,0.15) 0%, transparent 50%),
                radial-gradient(ellipse at 80% 80%, rgba(129,140,248,0.10) 0%, transparent 50%);
        }

        .page-wrapper {
            width: 100%;
            max-width: 900px;
            padding: 20px;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 0;
            min-height: 560px;
            box-shadow: 0 25px 80px rgba(0,0,0,0.6);
            border-radius: 20px;
            overflow: hidden;
        }

        /* ── Left hero panel ── */
        .hero {
            background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #6366f1 100%);
            padding: 48px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            position: relative;
            overflow: hidden;
        }
        .hero::before {
            content: '';
            position: absolute;
            top: -60px; right: -60px;
            width: 220px; height: 220px;
            background: rgba(255,255,255,0.08);
            border-radius: 50%;
        }
        .hero::after {
            content: '';
            position: absolute;
            bottom: -80px; left: -40px;
            width: 280px; height: 280px;
            background: rgba(255,255,255,0.05);
            border-radius: 50%;
        }
        .hero-logo {
            font-size: 2.6rem;
            margin-bottom: 8px;
        }
        .hero h1 {
            font-size: 2rem;
            font-weight: 700;
            color: #fff;
            margin-bottom: 12px;
            line-height: 1.2;
        }
        .hero p {
            font-size: 0.95rem;
            color: rgba(255,255,255,0.75);
            line-height: 1.6;
        }
        .hero-features {
            margin-top: 32px;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }
        .feature-item {
            display: flex;
            align-items: center;
            gap: 10px;
            color: rgba(255,255,255,0.85);
            font-size: 0.88rem;
        }
        .feature-icon { font-size: 1.1rem; }

        /* ── Right form panel ── */
        .form-panel {
            background: var(--card);
            padding: 40px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        /* Tabs */
        .tabs {
            display: flex;
            background: var(--surface);
            border-radius: 10px;
            padding: 4px;
            margin-bottom: 28px;
        }
        .tab-btn {
            flex: 1;
            padding: 10px;
            border: none;
            background: transparent;
            color: var(--muted);
            font-family: 'Inter', sans-serif;
            font-size: 0.875rem;
            font-weight: 500;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.2s;
        }
        .tab-btn.active {
            background: var(--accent);
            color: #fff;
            box-shadow: 0 4px 12px rgba(99,102,241,0.4);
        }

        /* Forms */
        .form-section { display: none; }
        .form-section.active { display: block; }

        .form-title {
            font-size: 1.4rem;
            font-weight: 700;
            color: var(--text);
            margin-bottom: 6px;
        }
        .form-subtitle {
            font-size: 0.85rem;
            color: var(--muted);
            margin-bottom: 24px;
        }

        label {
            display: block;
            font-size: 0.8rem;
            font-weight: 500;
            color: var(--muted);
            margin-bottom: 6px;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            background: var(--surface);
            border: 1.5px solid var(--border);
            border-radius: 10px;
            padding: 11px 14px;
            color: var(--text);
            font-family: 'Inter', sans-serif;
            font-size: 0.95rem;
            outline: none;
            transition: border-color 0.2s, box-shadow 0.2s;
            margin-bottom: 16px;
        }
        input:focus {
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(99,102,241,0.2);
        }

        .btn {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 10px;
            font-family: 'Inter', sans-serif;
            font-size: 0.95rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            letter-spacing: 0.02em;
        }
        .btn-primary {
            background: linear-gradient(135deg, var(--accent), #4f46e5);
            color: #fff;
            box-shadow: 0 4px 16px rgba(99,102,241,0.4);
        }
        .btn-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 6px 20px rgba(99,102,241,0.5);
        }
        .btn-primary:active { transform: translateY(0); }

        .alert {
            padding: 11px 14px;
            border-radius: 10px;
            font-size: 0.875rem;
            margin-bottom: 16px;
            font-weight: 500;
        }
        .alert-error   { background: rgba(239,68,68,0.12);  border: 1px solid rgba(239,68,68,0.3);  color: #fca5a5; }
        .alert-success { background: rgba(34,197,94,0.12);  border: 1px solid rgba(34,197,94,0.3);  color: #86efac; }

        @media (max-width: 640px) {
            .page-wrapper { grid-template-columns: 1fr; }
            .hero { display: none; }
        }
    </style>
</head>
<body>

<div class="page-wrapper">

    <!-- Hero Panel -->
    <div class="hero">
        <div class="hero-logo">💳</div>
        <h1>E-Wallet<br>System</h1>
        <p>Your secure digital wallet powered by Java Servlets &amp; MySQL.</p>
        <div class="hero-features">
            <div class="feature-item"><span class="feature-icon">🔒</span> Secure session management</div>
            <div class="feature-item"><span class="feature-icon">⚡</span> Instant transfers</div>
            <div class="feature-item"><span class="feature-icon">📊</span> Full transaction history</div>
            <div class="feature-item"><span class="feature-icon">🗄️</span> MySQL persistence</div>
        </div>
    </div>

    <!-- Form Panel -->
    <div class="form-panel">

        <!-- Tabs -->
        <div class="tabs">
            <button class="tab-btn active" id="tab-login"    onclick="switchTab('login')">Login</button>
            <button class="tab-btn"        id="tab-register" onclick="switchTab('register')">Register</button>
        </div>

        <!-- LOGIN FORM -->
        <div class="form-section active" id="section-login">
            <div class="form-title">Welcome back</div>
            <div class="form-subtitle">Sign in to your wallet</div>

            <% String loginError = (String) request.getAttribute("error"); %>
            <% if (loginError != null) { %>
                <div class="alert alert-error">⚠ <%= loginError %></div>
            <% } %>
            <% String regSuccess = (String) request.getAttribute("regSuccess"); %>
            <% if (regSuccess != null) { %>
                <div class="alert alert-success">✓ <%= regSuccess %></div>
            <% } %>

            <form action="<%= request.getContextPath() %>/login" method="post">
                <label for="login-user">Username</label>
                <input type="text" id="login-user" name="username"
                       placeholder="Enter your username"
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                       required autocomplete="username">

                <label for="login-pass">Password</label>
                <input type="password" id="login-pass" name="password"
                       placeholder="Enter your password"
                       required autocomplete="current-password">

                <button type="submit" class="btn btn-primary">Sign In →</button>
            </form>
        </div>

        <!-- REGISTER FORM -->
        <div class="form-section" id="section-register">
            <div class="form-title">Create account</div>
            <div class="form-subtitle">Join E-Wallet today</div>

            <% String regError = (String) request.getAttribute("regError"); %>
            <% if (regError != null) { %>
                <div class="alert alert-error">⚠ <%= regError %></div>
            <% } %>

            <form action="<%= request.getContextPath() %>/register" method="post">
                <label for="reg-user">Username</label>
                <input type="text" id="reg-user" name="username"
                       placeholder="Choose a username" required>

                <label for="reg-pass">Password</label>
                <input type="password" id="reg-pass" name="password"
                       placeholder="Choose a password" required>

                <label for="reg-confirm">Confirm Password</label>
                <input type="password" id="reg-confirm" name="confirm"
                       placeholder="Repeat your password" required>

                <button type="submit" class="btn btn-primary">Create Account →</button>
            </form>
        </div>

    </div>
</div>

<script>
    function switchTab(tab) {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.form-section').forEach(s => s.classList.remove('active'));
        document.getElementById('tab-' + tab).classList.add('active');
        document.getElementById('section-' + tab).classList.add('active');
    }

    // Auto-switch to register tab if there's a regError
    <% if (request.getAttribute("regError") != null) { %>
        switchTab('register');
    <% } %>
</script>

</body>
</html>
