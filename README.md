# 🏋️ Gym Management System

🌐 **Live Demo:** [https://gym-management-system-xmc8.onrender.com](https://gym-management-system-xmc8.onrender.com)
⚠️ *Note: This is hosted on a free server, so it may take a few seconds to load.*

---

## 📌 Project Overview

The Gym Management System is a full-stack web application developed to automate and digitalize the daily operations of a gym. It helps manage members, attendance, and administrative tasks efficiently.

Traditional gym systems rely on manual records or spreadsheets, which often lead to data duplication, data loss, and inefficiency. This project provides a **centralized and secure web-based solution** where all data is stored and managed efficiently.

---

## ✨ Features

### 👤 User Panel

* User registration and login
* View and update personal profile
* Check membership details
* Logout functionality

---

### 🛠️ Admin Panel

* Add, update, and delete members
* View all registered users
* Manage member records
* Monitor overall gym data

---

## 🏗️ System Architecture

The system follows **Spring Boot MVC architecture**:

* Thymeleaf handles UI (frontend + backend together)
* Controllers handle HTTP requests
* Services contain business logic
* Repository layer interacts with database

Flow:
User → Controller → Service → Repository → Database → View (Thymeleaf)

---

## 🛠️ Tech Stack

### 🔙 Backend (Core)

| Technology      | Purpose                     |
| --------------- | --------------------------- |
| Spring Boot     | Application framework       |
| Spring MVC      | Web architecture            |
| Spring Security | Authentication (Basic Auth) |
| Spring Data JPA | ORM / Database interaction  |
| Hibernate       | ORM implementation          |
| MySQL           | Relational database         |
| Thymeleaf       | Server-side rendering       |
| Lombok          | Reduce boilerplate code     |

---

## ⚙️ Modules

* 🔐 Authentication Module (Basic Authentication)
* 👥 User Management Module
* 📅 Attendance Management Module

---

## 🗄️ Database Design

The system uses a relational database with the following tables:

* Users
* Admin
* Membership
* Attendance

All tables are connected using **primary keys and foreign keys** to ensure data consistency.

---

## 🔐 Security

* Basic Authentication using Spring Security
* Role-based access control (Admin / User)
* Secure login validation
* Session management

---

## ⚙️ Setup & Installation

### 📌 Prerequisites

* Java 17+
* MySQL 8+
* Maven

---

### 🔧 Backend Setup

```bash
git clone https://github.com/yourusername/gym-management-system.git
cd gym-management-system
```

Create database:

```sql
create database gym;
```

Configure `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

Run application:

```bash
mvn spring-boot:run
```

👉 Runs on: [http://localhost:8080](http://localhost:8080)

---

## 🌐 Environment Configuration (Production)

The application uses environment variables for secure deployment.

### Required Variables:

#### 🗄️ Database

* MYSQLHOST
* MYSQLPORT
* MYSQLDATABASE
* MYSQLUSER
* MYSQLPASSWORD

#### 📧 Email

* MAIL_USERNAME
* MAIL_PASSWORD

#### 💳 Razorpay

* RAZORPAY_KEY
* RAZORPAY_SECRET

#### 🌐 Server

* PORT

---

## 🚀 Deployment

* Deployed on Render
* Uses environment variables
* Dynamic port binding supported

---

## 🚀 Advantages

* Reduces manual work
* Saves time and effort
* Improves data accuracy
* Centralized management
* Easy to use interface

---

## 🔮 Future Enhancements

* 💳 Payment management system
* 🏋️ Workout plan management
* 📧 Email & SMS notifications
* 📱 Mobile application
* 🤖 AI-based workout recommendations
* QR code-based attendance

---

## 👨‍💻 Author

**Nishant Mahajan**

---

## 📌 Conclusion

The Gym Management System is a modern web-based solution that replaces manual gym operations with a digital platform. It improves efficiency, ensures data accuracy, and provides a structured system for managing gym activities. The project is scalable and can be enhanced with advanced features in the future.
