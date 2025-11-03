# Student Records Management System (SRMS)

Simple Java 17 CLI app to manage student records with MySQL persistence, CSV import/export, sorting, searching, and a
small concurrency demo.

- Entry point: src/main/java/App.java
- DB config: src/main/java/db/DBConfig.java
- Build: Maven (pom.xml)

## Features

- Add, update, delete, list all students
- Search by name (case-insensitive)
- Sort by name, GPA, or department (asc/desc)
- Export to CSV and import from CSV
- Duplicate-email protection
- Thread-safe write operations with a simple concurrency simulation

## Tech Stack

- Java 17, Maven
- JDBC with MySQL Connector/J
- Logging: slf4j-simple

## Project Structure

- src/main/java/App.java — CLI menu and I/O
- src/main/java/model/Student.java — student domain model
- src/main/java/repo/* — DAO interfaces and JDBC implementation
- src/main/java/service/StudentService.java — business logic, caching, locking
- src/main/java/db/* — DB config and connection helper
- src/main/java/util/CSVUtil.java — CSV import/export

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.x (running locally)

## Database Setup

Run these in MySQL (adjust names if desired):

CREATE DATABASE srms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE srms_db;

CREATE TABLE students (
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
email VARCHAR(150) NOT NULL,
department VARCHAR(100) NOT NULL,
gpa DECIMAL(3,2) NOT NULL CHECK (gpa >= 0 AND gpa <= 4.00),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
UNIQUE KEY uq_students_email (email)
);

Update DB credentials in src/main/java/db/DBConfig.java:

public static final String URL  = "jdbc:mysql://localhost:3306/srms_db?useSSL=false&serverTimezone=UTC";
public static final String USER = "root"; // change if needed
public static final String PASS = "";     // change if needed

## Build

mvn -v
mvn -q -DskipTests compile

## Run

You can run without editing the POM by invoking the Exec plugin with coordinates:

# Linux/macOS
mvn -q -DskipTests org.codehaus.mojo:exec-maven-plugin:3.3.0:java -Dexec.mainClass=App

# Windows
mvn -q -DskipTests org.codehaus.mojo:exec-maven-plugin:3.3.0:java -Dexec.mainClass=App

Alternatively, add the plugin to your pom.xml for a shorter command:

  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.3.0</version>
      </plugin>
    </plugins>
    <!-- keep existing build/plugins if any -->
  </build>

Then run:

mvn -q -DskipTests exec:java -Dexec.mainClass=App

## Usage

From the menu:

- 1 Add Student: provide name, email, department, GPA (0–4)
- 2 Update Student: edit fields of an existing ID
- 3 Delete Student: remove by ID
- 4 View All: list all students
- 5 Search by Name: substring, case-insensitive
- 6 Sort: choose name|gpa|department and asc|desc
- 7 Export CSV: choose output file path
- 8 Import CSV: expects header id,name,email,department,gpa (duplicate emails skipped)
- 9 Simulate Concurrent Access: runs mixed read/write tasks safely
- 0 Exit

## CSV Format

- Header: id,name,email,department,gpa
- Export includes header; import expects header and ignores id values
- Fields with commas/quotes are quoted; embedded quotes are escaped

## Configuration

- DB connection: src/main/java/db/DBConfig.java
- Java version: set in pom.xml
- Dependencies: MySQL Connector/J 8.4.0, slf4j-simple 2.0.13

## Troubleshooting

- Communications link failure: ensure MySQL is running and DBConfig.URL is correct.
- Access denied: verify DBConfig.USER/PASS.
- Table doesn’t exist: run the DDL above to create students.
- DuplicateEmailException: email already exists (by design).
- Exec plugin not found: use the fully qualified plugin command shown above or add the plugin block to pom.xml.