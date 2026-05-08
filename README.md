# E-Wallet Web App

A simple web-based e-wallet system built using Java Servlets and JSP. Users can register, log in, deposit money, and transfer funds between accounts.

## Tech Stack

- Java (Servlets + JSP)
- MySQL
- Apache Tomcat
- XAMPP

## Requirements

Before running, make sure you have:

- JDK 8 or above
- XAMPP (with MySQL and Tomcat)
- MySQL Connector JAR (already included in `WebContent/WEB-INF/lib/`)

## How to Run

1. Start XAMPP and make sure **MySQL** and **Tomcat** are running.

2. Import the database:
   - Open phpMyAdmin (`http://localhost/phpmyadmin`)
   - Create a database named `ewallet`
   - Import the SQL file if provided

3. Build and deploy the project:
   ```
   build.bat
   ```
   This will compile the code, create a WAR file, and deploy it to Tomcat automatically.

4. Open your browser and go to:
   ```
   http://localhost:8080/ewallet/
   ```

That's it. You should see the login page.

## Notes

- Tomcat path is set to `D:\xampp\tomcat` in `build.bat`. Change it if your XAMPP is installed somewhere else.
- Default port is `8080`.
