# Authentication Debugging Guide for Azure VM

## Current Issue
Getting "Unauthorized error: Full authentication is required to access this resource" errors in Azure VM deployment.

## Debugging Steps

### 1. Test Public Endpoints First
```bash
# Test if the application is running
curl -X GET http://172.187.160.142:8080/api/test/public

# Test health endpoint
curl -X GET http://YOUR_AZURE_VM_IP:8080/actuator/health
```

### 2. Test Authentication Flow
```bash
# Step 1: Login to get JWT token
curl -X POST http://YOUR_AZURE_VM_IP:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@example.com",
    "password": "your-password"
  }'

# Step 2: Use the token from response in subsequent requests
curl -X GET http://YOUR_AZURE_VM_IP:8080/api/test/protected \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 3. Check Common Issues

#### A. Environment Variables
Ensure these environment variables are set in Azure VM:
```bash
export JWT_SECRET="your-jwt-secret-here"
export JWT_EXPIRATION_MS="86400000"
export SPRING_DATASOURCE_URL="jdbc:postgresql://db:5432/HMS"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="your-db-password"
export SPRING_JPA_HIBERNATE_DDL_AUTO="create-drop"
export SPRING_JPA_SHOW_SQL="true"
```

**⚠️ IMPORTANT**: If using `create-drop` for `SPRING_JPA_HIBERNATE_DDL_AUTO`, the database schema will be recreated on every startup. This can cause startup failures if:
- Database initialization takes too long
- There are existing data constraints
- Multiple instances try to create schema simultaneously

For production, consider using `update` or `validate` instead.

#### B. CORS Configuration
The current CORS config allows all origins (`*`). If still having issues, check:
- Frontend is sending requests to correct URL
- Authorization header is being sent properly

#### C. JWT Token Format
Ensure frontend is sending Authorization header as:
```
Authorization: Bearer <jwt-token>
```
NOT:
```
Authorization: <jwt-token>
```

### 4. Frontend Checklist

If you're using JavaScript/React frontend, ensure:

```javascript
// Correct way to send JWT token
const token = localStorage.getItem('token'); // or however you store it

fetch('http://your-azure-vm:8080/api/protected-endpoint', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

### 5. Check Docker Logs
```bash
# Check application logs
docker-compose logs hms-backend

# Follow logs in real-time
docker-compose logs -f hms-backend
```

### 6. Database Connection
Ensure PostgreSQL is running and accessible:
```bash
# Check if PostgreSQL container is running
docker-compose ps

# Test database connection
docker-compose exec db psql -U postgres -d HMS -c "SELECT current_database();"
```

## Quick Fixes to Try

### Fix 1: Restart Services
```bash
cd /path/to/your/docker-compose
docker-compose down
docker-compose up -d
```

### Fix 2: Check Port Accessibility
Ensure port 8080 is open in Azure VM:
```bash
# Check if port is listening
netstat -tulpn | grep 8080

# Test locally on VM
curl http://localhost:8080/api/test/public
```

### Fix 3: Update Docker Compose (if using domain)
If accessing via domain name, ensure CORS allows your domain:
```yaml
environment:
  - ALLOWED_ORIGINS=http://your-domain.com,https://your-domain.com
```

## Expected Behavior After Fixes

1. `/api/test/public` should return success without authentication
2. `/api/auth/signin` should return JWT token
3. `/api/test/protected` should work with valid JWT token
4. Unauthorized requests should show detailed logging in application logs

## Log Analysis

With the enhanced logging added, you should see:
- Request details (method, URI, JWT presence)
- JWT validation results
- Authentication success/failure details

Look for patterns in the logs to identify root cause.

## Container Startup Failures

### Issue: Backend container exits with status 1

This typically happens when:
1. Database connection fails
2. Environment variables are missing or incorrect
3. JPA configuration issues with `create-drop`

### Debugging Steps:

```bash
# Check container logs for detailed error
docker-compose logs hms-backend

# Check if database is accessible
docker-compose exec hms-backend ping db

# Verify environment variables are loaded correctly
docker-compose exec hms-backend env | grep SPRING
```

### Common Fixes:

#### Fix 1: Database Connection Issues
```bash
# Ensure database is healthy before starting backend
docker-compose up -d db
docker-compose ps  # Wait for db to be healthy
docker-compose up -d backend
```

#### Fix 2: JPA DDL-Auto Issues
If using `create-drop`, the application recreates the schema on every restart. This can fail if:
- There are existing connections to the database
- The application doesn't have sufficient permissions
- Database initialization takes too long

Quick fix - change to `update` in your `.env` file:
```bash
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

#### Fix 3: JWT Secret Generation
The JWT secret in `.env` uses a shell command that may not execute properly in Docker:
```bash
# Instead of: JWT_SECRET=$(openssl rand -base64 48)
# Use a static value for deployment:
JWT_SECRET=your-base64-encoded-secret-here
```
