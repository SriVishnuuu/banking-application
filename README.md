@'
# VISHNU BANK — Banking Application

A full-stack banking application built with Java and Spring Boot, designed to demonstrate real-world banking workflows, secure authentication, account management, and transaction processing.

## Features

- User registration and login
- JWT-based authentication
- Customer profile management
- Bank account creation and account management
- Beneficiary management
- Money transfers and withdrawals
- Transaction history and transaction limits
- Administrative management of customers, accounts, beneficiaries, branches, and transactions
- Audit logging
- Password reset via email

## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- JWT authentication
- MapStruct

### Database
- MySQL

### Frontend
- React
- Vite
- JavaScript
- HTML and CSS

## Architecture

The backend follows a modular layered-monolith architecture, organized by business domain.

Request flow:

Client → Controller → Service → Repository → MySQL

Business domains include users, customers, accounts, beneficiaries, branches, transactions, administration, security, and audit logging.

## Configuration

Configure the following environment variables before running the backend:

- `DB_URL` — JDBC URL for the MySQL database
- `DB_USERNAME` — MySQL username
- `DB_PASSWORD` — MySQL password
- `JWT_SECRET` — Secret used for JWT operations
- `MAIL_USERNAME` — Email account used for password-reset notifications
- `MAIL_PASSWORD` — Email account password or app password
- `APP_PASSWORD_RESET_URL` — Frontend password-reset URL

Never commit real passwords, tokens, or secrets to source control.

## Run Locally

### Prerequisites
- JDK 21
- MySQL 8 or compatible version
- Git

### Steps

1. Create a MySQL database named `banking_db`.
2. Configure the required environment variables.
3. Open a terminal in the backend project directory.
4. Run the application:

   ```powershell
   .\mvnw.cmd spring-boot:run