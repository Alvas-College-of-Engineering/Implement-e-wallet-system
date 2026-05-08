package com.ewallet.servlet;

import com.ewallet.dao.DatabaseManager;
import com.ewallet.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirm");

        if (username == null || username.isBlank()) {
            req.setAttribute("regError", "Username cannot be empty.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp); return;
        }
        if (password == null || password.isBlank()) {
            req.setAttribute("regError", "Password cannot be empty.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp); return;
        }
        if (!password.equals(confirm)) {
            req.setAttribute("regError", "Passwords do not match.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp); return;
        }

        try {
            if (DatabaseManager.usernameExists(username.trim())) {
                req.setAttribute("regError", "Username already taken. Choose another.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp); return;
            }
            DatabaseManager.createUser(username.trim(), password, 0.0);
            req.setAttribute("regSuccess", "Account created! You can now log in.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("regError", "Registration failed: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
