package com.ewallet.servlet;

import com.ewallet.dao.DatabaseManager;
import com.ewallet.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /** GET /login → show login page */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Already logged in? go to dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    /** POST /login → authenticate */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            req.setAttribute("error", "Please enter username and password.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = DatabaseManager.authenticate(username.trim(), password);
            if (user != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(30 * 60); // 30 min
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("error", "Invalid username or password.");
                req.setAttribute("username", username);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
