# Dormie Backend - Hotel Management System

A comprehensive backend system for managing dormitory/hotel operations built with Spring Boot.

## Features

- Student registration and management
- Hall fee and dining fee management
- Payment processing with SSLCommerz integration
- JWT-based authentication
- File upload capabilities
- REST API endpoints

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional, for containerized deployment)

## Setup Instructions

### 1. Environment Configuration

1. Copy the environment template:
   ```bash
   cp .env.example .env
   ```

2. Update the `.env` file with your actual configuration:
   - Database credentials
   - JWT secret key
   - SSLCommerz payment gateway credentials
   - Other environment-specific settings

### 2. Database Setup

1. Create a PostgreSQL database named `HMS`
2. Update the database configuration in `.env` file

### 3. Running the Application

#### Option A: Using Maven (Development)

```bash
mvn spring-boot:run
```

#### Option B: Using Docker (Production)

1. Copy the docker script templates:
   ```bash
   cp docker_run.sh.example docker_run.sh
   cp docker_stop.sh.example docker_stop.sh
   ```

2. Update `docker_run.sh` with your actual environment variables

3. Make scripts executable:
   ```bash
   chmod +x docker_run.sh docker_stop.sh
   ```

4. Run the application:
   ```bash
   ./docker_run.sh
   ```

5. Stop the application:
   ```bash
   ./docker_stop.sh
   ```

## API Endpoints

The application will be available at `http://localhost:8080`

- Health check: `GET /actuator/health`
- Authentication: `POST /api/auth/login`
- Student management: `/api/students/**`
- Fee management: `/api/fees/**`
- Payment processing: `/api/payments/**`

## Development

### Building the Project

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Creating JAR

```bash
mvn clean package
```

## Security Note

- Never commit sensitive files like `.env`, `docker_run.sh`, `docker_stop.sh`, or `login.txt`
- Use the provided `.example` files as templates
- Ensure all secrets are properly configured in your environment

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## License

[Add your license information here]
