# Auction API

## Introduction

This project is an Auction API built with Spring Boot. It allows users to create, bid, and manage auctions.

## Prerequisites

- Java 21
- Maven 3.9
- MySQL 8.0

## Getting Started

### Setup Database

1. Create a database named `auction_app` in MySQL.

    ```sql
    CREATE DATABASE auction_app;
    ```

2. Import the `auctiondb.sql` file to populate your database.

    ```bash
    mysql -u root -p auction_app < auctiondb.sql
    ```

3. Ensure that you have a MySQL user with the necessary privileges. Update the username and password in the `application.properties` file accordingly.

### Configuration

Open the `src/main/resources/application.properties` file and configure the following properties:

```properties
# Application Name
spring.application.name=auction-api

# Database Configuration
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/auction_app
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# Hibernate Configuration
spring.jpa.properties.hibernate.format-sql=true
spring.jpa.properties.hibernate.show-sql=true

# JWT Configuration
spring.security.jwt.secret-key=change_to_your_secret_key
spring.security.jwt.expired-in-ms=86400000

# Quartz Configuration
spring.quartz.job-store-type=jdbc
spring.quartz.jdbc.initialize-schema=always
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
spring.quartz.properties.org.quartz.scheduler.instanceName=AuctionScheduler
spring.quartz.properties.org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
```

### Build and Run the Application
1. Navigate to the project directory.
```bash
	cd auction-api
```
2. Build the project using Maven.
```bash
	mvn clean install
```
3. Run the application.
```bash
	mvn spring-boot:run
```
### Running Test
```bash
	mvn test
```

### API Documentation

For detailed API documentation, you can import the provided Postman collection:

    Open Postman.
    Click on Import in the top-left corner.
    Select Upload Files.
    Choose the Auction-API-Spring.postman_collection.json file located in the root directory of the project.
    Click Import.

This will import all the available API endpoints into Postman, allowing you to easily test and explore the API.






