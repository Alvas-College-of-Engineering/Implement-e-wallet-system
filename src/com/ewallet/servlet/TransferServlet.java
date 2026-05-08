package com.ewallet.servlet;

import com.ewallet.dao.DatabaseManager;
import com.ewallet.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }

        User sender       = (User) session.getAttribute("user");
        String recipient  = req.getParameter("recipient");
        String amountStr  = req.getParameter("amount");

        if (recipient == null || recipient.isBlank()) {
            session.setAttribute("flash_error", "Recipient username is required.");
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }
        if (recipient.equalsIgnoreCase(sender.getUsername())) {
            session.setAttribute("flash_error", "You cannot transfer to yourself.");
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }

        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                session.setAttribute("flash_error", "Amount must be positive.");
                resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
            }

            User recipientUser = DatabaseManager.getUserByUsername(recipient.trim());
            if (recipientUser == null) {
                session.setAttribute("flash_error", "User \"" + recipient + "\" not found.");
                resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
            }

            boolean success = DatabaseManager.transfer(sender.getId(), recipientUser.getId(), amount);
            if (success) {
                session.setAttribute("flash_success",
                        String.format("₹%.2f transferred to %s successfully!",
                                amount, recipientUser.getUsername()));
            } else {
                session.setAttribute("flash_error", "Insufficient balance for transfer.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("flash_error", "Invalid amount entered.");
        } catch (SQLException e) {
            session.setAttribute("flash_error", "Database error: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
