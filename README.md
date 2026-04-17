# Credit Card Module - Financial Data Management System

##  Overview
This project is a credit card management module designed to handle financial data such as transactions, invoices, and credit limits. It focuses on data consistency, business rules, and relational modeling.

##  Data Engineering Perspective
Key data-related aspects:
- Relational database modeling
- Transaction processing and consistency
- Financial data validation and integrity
- ORM-based data persistence (JPA/Hibernate)

##  Architecture
- Backend: Java + Spring Boot
- Architecture: Onion Architecture
- Database: Relational (SQL)
- ORM: JPA (Hibernate)

##  Data Modeling

Main Entities:
- **User**
- **CreditCard**
- **Transaction**
- **Invoice**
- **Payment**

Relationships:
- User → CreditCard (1:N)
- CreditCard → Transactions (1:N)
- CreditCard → Invoice (1:N)
- Invoice → Payment (1:N)

##  Data Processing

The system handles:
- Transaction recording and validation
- Invoice generation and updates
- Payment processing with balance adjustments
- Credit limit analysis and updates

##  Data Integrity & Rules
- Prevents transactions without available limit
- Ensures invoice consistency after payments
- Validates business rules for financial operations

##  Technologies
- Java
- Spring Boot
- JPA / Hibernate
- SQL Database

##  Testing
- Unit tests with JUnit and Mockito
- Validation of business rules and data integrity

##  Key Features
- Credit card lifecycle management
- Transaction history tracking
- Invoice generation and payment
- Credit analysis logic

##  Future Improvements
- Data warehouse integration
- Fraud detection using data patterns
- Real-time analytics dashboard

## 🔗 Repository
https://github.com/arthurczo/CreditCard.git