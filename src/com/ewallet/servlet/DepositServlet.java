package com.ewallet.servlet;

import com.ewallet.dao.DatabaseManager;
import com.ewallet.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/deposit")
public class DepositServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }

        User user = (User) session.getAttribute("user");
        String amountStr = req.getParameter("amount");

        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                session.setAttribute("flash_error", "Amount must be positive.");
            } else {
                DatabaseManager.deposit(user.getId(), amount);
                session.setAttribute("flash_success",
                        String.format("₹%.2f deposited successfully!", amount));
            }
        } catch (NumberFormatException e) {
            session.setAttribute("flash_error", "Invalid amount entered.");
        } catch (SQLException e) {
            session.setAttribute("flash_error", "Database error: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
