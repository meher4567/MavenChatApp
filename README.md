# Maven Chat App

A Java desktop chat application using Swing, sockets, and MySQL.

## Project Summary
- Real-time one-to-one chat over TCP sockets
- Offline message persistence and delivery
- User registration and login
- Friend requests and friend list management
- Basic profile update support

## Tech Stack
- Java 11
- Maven
- Swing
- MySQL
- JDBC + HikariCP

## Security and Maintenance Fixes Applied
- Upgraded project build target to Java 11
- Removed hardcoded database credentials
- Added config loading through environment variables or JVM properties
- Updated password hashing to salted PBKDF2
- Kept login compatibility with previously stored SHA-256 hashes
- Fixed offline message retrieval path on server side

## Prerequisites
- Java 11+
- Maven 3.8+
- MySQL 8+

## Database Configuration
The app reads DB settings in this order:
1. JVM system properties
2. Environment variables
3. Default URL only (for DB URL)

### Environment variables (recommended)
- CHAT_DB_URL (optional, default: jdbc:mysql://localhost:3306/chatting_app)
- CHAT_DB_USER (required)
- CHAT_DB_PASSWORD (required)

PowerShell example:

```powershell
$env:CHAT_DB_URL="jdbc:mysql://localhost:3306/chatting_app"
$env:CHAT_DB_USER="root"
$env:CHAT_DB_PASSWORD="your_password"
```

### JVM properties
-Dchat.db.url=jdbc:mysql://localhost:3306/chatting_app
-Dchat.db.user=root
-Dchat.db.password=your_password

## Minimal MySQL Schema
Create database `chatting_app`, then run:

```sql
CREATE TABLE users (
  username VARCHAR(100) PRIMARY KEY,
  password VARCHAR(512) NOT NULL,
  email VARCHAR(255) UNIQUE
);

CREATE TABLE allUsers (
  username VARCHAR(100) PRIMARY KEY
);

CREATE TABLE profile (
  username VARCHAR(100) PRIMARY KEY,
  about TEXT,
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE friends (
  username VARCHAR(100) NOT NULL,
  friendname VARCHAR(100) NOT NULL,
  PRIMARY KEY (username, friendname)
);

CREATE TABLE friend_requests (
  sender VARCHAR(100) NOT NULL,
  receiver VARCHAR(100) NOT NULL,
  PRIMARY KEY (sender, receiver)
);

CREATE TABLE messages (
  message_id INT PRIMARY KEY AUTO_INCREMENT,
  sender VARCHAR(100) NOT NULL,
  receiver VARCHAR(100) NOT NULL,
  message_text TEXT NOT NULL,
  message_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  message_type VARCHAR(20) NOT NULL
);
```

## Build
```bash
mvn clean compile
```

## Quick Start
1. Start MySQL and create the schema above.
2. Set database environment variables.
3. Run `backend.ChatServer`.
4. Run one or more instances of `frontend.ChatApplicationWindow`.

Start the server first, then clients.

## Main Entry Points
- backend.ChatServer
- frontend.ChatApplicationWindow
