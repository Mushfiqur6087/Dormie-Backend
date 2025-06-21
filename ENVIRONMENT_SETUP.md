# Environment Setup

This project uses environment variables to manage sensitive configuration data. Follow these steps to set up your environment:

## 1. Create Environment File

Copy the example environment file and customize it with your actual values:

```bash
cp .env.example .env
```

## 2. Configure Environment Variables

Edit the `.env` file and replace the placeholder values with your actual configuration:

- **Database Configuration**: Update with your PostgreSQL database credentials
- **JWT Secret**: Generate a secure base64-encoded secret for JWT token signing
- **SSLCommerz**: Configure with your actual SSLCommerz payment gateway credentials
- **Other Settings**: Adjust file upload directory, geocoding settings, etc.

## 3. Security Notes

- **Never commit the `.env` file to version control** - it contains sensitive credentials
- The `.env` file is already included in `.gitignore`
- Use `.env.example` as a template for new environments
- In production, use proper secret management systems instead of environment files

## 4. Loading Environment Variables

### Option 1: Using an IDE
Most IDEs can load environment variables from `.env` files automatically or with plugins.

### Option 2: Using dotenv in Spring Boot (Recommended)
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>2.5.4</version>
</dependency>
```

### Option 3: Manual Loading
If not using dotenv, you can load the environment variables manually:

```bash
# Load environment variables before running the application
export $(cat .env | xargs)
mvn spring-boot:run
```

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL database URL | `jdbc:postgresql://localhost:5432/HMS` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `your_password` |
| `JWT_SECRET` | Base64 encoded JWT signing secret | `your_base64_secret` |
| `JWT_EXPIRATION_MS` | JWT token expiration in milliseconds | `86400000` |
| `SSLCOMMERZ_STORE_ID` | SSLCommerz store ID | `your_store_id` |
| `SSLCOMMERZ_STORE_PASSWORD` | SSLCommerz store password | `your_store_password` |
| `SSLCOMMERZ_ENVIRONMENT` | SSLCommerz environment | `sandbox` or `production` |

For a complete list of all environment variables, see the `.env.example` file.
