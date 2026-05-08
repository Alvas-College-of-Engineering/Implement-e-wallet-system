package com.ewallet.servlet;

import com.ewallet.dao.DatabaseManager;
import com.ewallet.model.Transaction;
import com.ewallet.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        try {
            // Always refresh balance and history from DB
            User fresh = DatabaseManager.refreshUser(user.getId());
            List<Transaction> txns = DatabaseManager.getTransactions(fresh.getId());
            fresh.setHistory(txns);
            session.setAttribute("user", fresh);   // update session
            req.setAttribute("transactions", txns);
        } catch (SQLException e) {
            req.setAttribute("dbError", "Could not load data: " + e.getMessage());
        }

        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
