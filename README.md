# Dormie - Hall Management System (HMS) Backend

## Overview

**Dormie** is an advanced Hall Management System designed for university dormitories and residential halls. It provides a comprehensive solution for managing student accommodations, dining fees, hall fees, applications, and payments.

### What HMS Does

The Hall Management System (HMS) provides the following core functionalities:

#### 🏠 **Student Management**
- Student registration and profile management
- User authentication with JWT-based security
- Role-based access control (Admin, Student)
- Student information tracking including personal details, department, batch, contact information

#### 🏛️ **Hall Administration**
- Hall application processing and approval workflow
- Residency status management (attached/detached)
- Student accommodation tracking
- Hall capacity and availability management

#### 💰 **Fee Management**
- **Hall Fees**: Monthly/semester accommodation charges
- **Dining Fees**: Meal plan and cafeteria charges
- Fee structure creation and management by administrators
- Individual student fee tracking and payment status

#### 💳 **Payment Processing**
- Integrated payment gateway using SSLCommerz
- Secure online payment processing
- Payment history and transaction tracking
- Automatic fee calculation and billing

#### 📋 **Application System**
- Hall application submission by students
- Document upload support (photos, certificates)
- Application review and approval workflow
- Application status tracking

#### 👨‍💼 **Administrative Tools**
- Comprehensive admin dashboard
- Student data management and reporting
- Fee structure configuration
- Payment monitoring and reconciliation
- Bulk operations for student management

## Technology Stack

- **Backend Framework**: Spring Boot 3.4.5
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Security**: Spring Security with JWT authentication
- **Payment Gateway**: SSLCommerz
- **Documentation**: Spring Boot Actuator
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## Prerequisites

Before running the application, ensure you have the following installed:

- **Docker**: Version 20.0 or higher
- **Docker Compose**: Version 2.0 or higher
- **Java 17**: (if running without Docker)
- **Maven 3.6+**: (if building without Docker)
- **PostgreSQL 15**: (if running database separately)

## Quick Start with Docker (Recommended)

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Dormie-Backend
```

### 2. Environment Configuration
Create a `.env` file in the root directory:
```bash
# Database Configuration
POSTGRES_DB=HMS
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION_MS=86400000

# SSLCommerz Payment Gateway
SSLCOMMERZ_STORE_ID=your_store_id
SSLCOMMERZ_STORE_PASSWORD=your_store_password
SSLCOMMERZ_ENVIRONMENT=sandbox

# Application Settings
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

### 3. Start the Application
```bash
# Make the script executable
chmod +x docker_run.sh

# Start the application
./docker_run.sh
```

The script will:
- Build the Docker images
- Start PostgreSQL database
- Start the Spring Boot application
- Perform health checks
- Display service status

### 4. Verify Installation
Once started, the application will be available at:
- **API Base URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Database**: localhost:5433 (PostgreSQL)

## Manual Setup (Without Docker)

### 1. Database Setup
```bash
# Install and start PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Create database
sudo -u postgres createdb HMS
sudo -u postgres createuser --interactive
```

### 2. Configure Application
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/HMS
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run
```bash
# Build the application
./mvnw clean package

# Run the application
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## API Documentation

### Authentication Endpoints
```http
POST /api/auth/signin          # User login
POST /api/auth/signup          # User registration
```

### Student Management
```http
GET    /api/students           # Get all students (Admin only)
GET    /api/students/profile   # Get current student profile
PUT    /api/students/profile   # Update student profile
POST   /api/students           # Create new student (Admin only)
```

### Hall Applications
```http
POST   /api/applications       # Submit hall application
GET    /api/applications       # Get applications (Admin: all, Student: own)
PUT    /api/applications/{id}  # Update application status (Admin only)
GET    /api/applications/{id}  # Get application details
```

### Fee Management
```http
# Hall Fees
GET    /api/hall-fees          # Get hall fee structures
POST   /api/hall-fees          # Create hall fee (Admin only)
GET    /api/students/hall-fees # Get student's hall fees
POST   /api/students/hall-fees # Assign hall fee to student

# Dining Fees
GET    /api/dining-fees        # Get dining fee structures
POST   /api/dining-fees        # Create dining fee (Admin only)
GET    /api/students/dining-fees # Get student's dining fees
POST   /api/students/dining-fees # Assign dining fee to student
```

### Payment Processing
```http
POST   /api/payments/initiate  # Initiate payment
POST   /api/payments/success   # Payment success callback
POST   /api/payments/fail      # Payment failure callback
```

## Testing

### Automated Tests
```bash
# Run unit tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=StudentCreateUpdateTest
```

### Integration Testing
The project includes shell scripts for testing different workflows:

```bash
# Test hall fee workflow
chmod +x "test scripts/test_hall_fees.sh"
"./test scripts/test_hall_fees.sh"

# Test dining fee workflow
chmod +x "test scripts/test_dining_fees.sh"
"./test scripts/test_dining_fees.sh"
```

## Default Admin Account

The system creates a default admin account on startup:
- **Email**: admin@dormie.com
- **Password**: Admin123!

**Important**: Change the default admin password after first login in production.

## File Uploads

The system supports file uploads for:
- Student profile photos
- Application documents
- Supporting certificates

Files are stored in the `./uploads` directory by default.

## Monitoring and Health Checks

- **Health Endpoint**: `/actuator/health`
- **Application Info**: `/actuator/info`
- **Metrics**: Available through Spring Boot Actuator

## Development

### Project Structure
```
src/
├── main/java/com/HMS/hms/
│   ├── Controller/          # REST API controllers
│   ├── Service/            # Business logic services
│   ├── Repo/               # Data repositories
│   ├── Tables/             # JPA entities
│   ├── DTO/                # Data transfer objects
│   ├── Security/           # Authentication & authorization
│   ├── Payment/            # Payment gateway integration
│   ├── enums/              # Enumeration types
│   └── config/             # Configuration classes
└── resources/
    ├── application.properties
    └── static/             # Static web resources
```


### Adding New Features
1. Create entity in `Tables/` package
2. Create repository in `Repo/` package
3. Implement service in `Service/` package
4. Create controller in `Controller/` package
5. Add DTOs in `DTO/` package if needed

## Deployment

### Production Deployment
1. Set `SSLCOMMERZ_ENVIRONMENT=production`
2. Use strong JWT secret and database passwords
3. Enable HTTPS
4. Configure proper backup strategies
5. Set up monitoring and logging

### Environment Variables for Production
```bash
JWT_SECRET=your_strong_production_secret
POSTGRES_PASSWORD=strong_database_password
SSLCOMMERZ_STORE_ID=production_store_id
SSLCOMMERZ_STORE_PASSWORD=production_store_password
SSLCOMMERZ_ENVIRONMENT=production
```

## Stopping the Application

```bash
# Stop Docker containers
./docker_stop.sh

# Or manually
docker compose down
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Change ports in `docker-compose.yml` if 8080 or 5433 are occupied
2. **Database connection**: Ensure PostgreSQL is running and credentials are correct
3. **Permission denied**: Make shell scripts executable with `chmod +x`
4. **Memory issues**: Increase Docker memory allocation if build fails

### Logs
```bash
# View application logs
docker compose logs backend

# View database logs
docker compose logs db

# Follow logs in real-time
docker compose logs -f backend
```

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the test scripts for API usage examples

---

**Note**: This is a development/educational project. For production use, ensure proper security configurations, monitoring, and backup strategies are implemented.
