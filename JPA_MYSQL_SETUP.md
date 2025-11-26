# Spring Data JPA + MySQL Setup Guide

## 📋 Tổng quan

Project đã được cấu hình sử dụng:
- **Spring Data JPA** - ORM framework
- **MySQL** - Database
- **Hibernate** - JPA implementation
- **H2** - In-memory database cho testing

---

## 🗄️ Database Schema

### Tables Created Automatically:

#### 1. **departments** table
```sql
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME
);
```

#### 2. **employees** table
```sql
CREATE TABLE employees (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    department_id BIGINT,
    position VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
```

---

## 🚀 Setup Instructions

### 1. Install MySQL

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

**macOS:**
```bash
brew install mysql
brew services start mysql
```

**Windows:**
Download from: https://dev.mysql.com/downloads/mysql/

---

### 2. Create Database

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE employee_management_db;

# Create user (optional)
CREATE USER 'empuser'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON employee_management_db.* TO 'empuser'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

### 3. Configure application.properties

File: `src/main/resources/application.properties`

```properties
# Change these if needed:
spring.datasource.username=root
spring.datasource.password=root  # Your MySQL password
```

### 4. Build and Run

```bash
# Build project
./mvnw clean install

# Run application
./mvnw spring-boot:run
```

---

## 📊 Entity Relationships

```
Department (1) ←──── (Many) Employee
    ↓                       ↓
    id (PK)                id (PK)
    name                   name
    description            email
    employees (List)       phone
                          department_id
                          position
```

---

## 🧪 Testing API Endpoints

### Department APIs

#### 0. Login
```bash
# Tạo user thường (role USER)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "role": "USER"
  }'

# Tạo user admin (role ADMIN)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newadmin",
    "password": "admin456",
    "role": "ADMIN"
  }'

# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "newadmin", "password": "admin456"}'

# Login as user
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "newuser", "password": "password123"}'
```


#### 1. Create Department
```bash
curl -X POST http://localhost:8080/api/departments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "Engineerin 2",
    "description": "Engineering Department 2"
  }'
```

#### 2. Get All Departments
```bash
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" http://localhost:8080/api/departments
```

#### 3. Get Department by ID
```bash
curl -X GET http://localhost:8080/api/employees \
 -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### 4. Update Department by ID
```bash
curl -X PUT http://localhost:8080/api/departments/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "IT Update",
    "description": "Information Technology Department Update"
  }'
```

#### 5. Destroy Department by ID
```bash
curl -X DELETE http://localhost:8080/api/departments/3 -H "Authorization: Bearer YOUR_TOKEN_HERE"
```


### Employee APIs

#### 1. Create Employee
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "john doe",
    "email": "john@example.com",
    "phone": "0912345678",
    "department": {
      "id": 1
    },
    "position": "Developer"
  }'
```

#### 2. Search All Employees
```bash
curl http://localhost:8080/api/employees?name=J&departmentName=IT -H "Authorization: Bearer YOUR_TOKEN_HERE"
```
**Response Example:**
```json
[
  {
    "id": "EMP-20251114-1001",
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "(091) 234-5678",
    "department": {
      "id": 1,
      "name": "IT",
      "description": "Information Technology Department"
    },
    "position": "Developer",
    "createdAt": "2025-11-14T10:00:00",
    "updatedAt": "2025-11-14T10:00:00"
  },
  {
    "id": "EMP-20251114-1002",
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phone": "(098) 765-4321",
    "department": {
      "id": 1,
      "name": "IT",
      "description": "Information Technology Department"
    },
    "position": "Senior Developer",
    "createdAt": "2025-11-14T11:00:00",
    "updatedAt": "2025-11-14T11:00:00"
  }
]
```

#### 3. Get Employees in a Department
```bash
curl http://localhost:8080/api/departments/1/employees -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### 4. Update Employee
```bash
curl -X PUT http://localhost:8080/api/employees/EMP-20251113-0001 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "john doe update",
    "email": "johnuu@example.com",
    "phone": "0912345678",
    "department": {
      "id": 1
    },
    "position": "Developer"
  }'
```

#### 5. Destroy Employees
```bash
curl -X DELETE http://localhost:8080/api/employees/EMP-20251113-0001 -H "Authorization: Bearer YOUR_TOKEN_HERE"
```




###

---

## 🔧 Configuration Properties

```properties
# DDL Auto modes:
spring.jpa.hibernate.ddl-auto=update
# - create: Drop và tạo lại tables mỗi lần run
# - create-drop: Tạo tables, xóa khi shutdown
# - update: Cập nhật schema (recommended for dev)
# - validate: Chỉ validate schema
# - none: Không làm gì

# Show SQL queries
spring.jpa.show-sql=true

# Format SQL output
spring.jpa.properties.hibernate.format_sql=true

# MySQL Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```
---
