package com.ewallet.listener;

import com.ewallet.dao.DatabaseManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;

/**
 * Initializes the database when the web application starts.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatabaseManager.initialize();
            System.out.println("[App] Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[App] FATAL: Could not initialize database: " + e.getMessage());
            throw new RuntimeException("Database initialization failed.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[App] Application shutting down.");
    }
}
