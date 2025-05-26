# Spring Security with JWT Authentication - Implementation Guide

## Overview
This implementation provides a complete Spring Security configuration with JWT (JSON Web Token) authentication for the HMS (Hall Management System). It replaces the previous plain text authentication with secure, token-based authentication.

## Key Features

### 1. **JWT Token Authentication**
- Secure token-based authentication
- Configurable token expiration (24 hours by default)
- HMAC SHA-256 signing algorithm
- Base64 encoded secret key

### 2. **Password Security**
- BCrypt password encoding
- Automatic migration from plain text passwords
- Secure password validation

### 3. **Role-Based Access Control**
- Five user roles: ADMIN, HALL_MANAGER, STUDENT, AUTHORITY, SUPERVISOR
- Method-level security with `@PreAuthorize`
- Fine-grained access control

### 4. **CORS Configuration**
- Cross-origin resource sharing enabled
- Configurable for frontend integration
- Supports credentials and all HTTP methods

## API Endpoints

### Authentication Endpoints

#### 1. Sign In (JWT)
```http
POST /api/auth/signin
Content-Type: application/json

{
    "email": "admin@dormie.com",
    "password": "admin123"
}
```

**Success Response:**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "id": 1,
    "username": "admin",
    "email": "admin@dormie.com",
    "roles": ["ROLE_ADMIN"]
}
```

#### 2. Sign Up
```http
POST /api/auth/signup
Content-Type: application/json

{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "role": "student"
}
```

**Success Response:**
```json
{
    "message": "User registered successfully!"
}
```

#### 3. Legacy Login (Backward Compatibility)
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "admin@dormie.com",
    "password": "admin123"
}
```

### Protected Endpoints

#### Test Endpoints
- `GET /api/test/all` - Public access
- `GET /api/test/user` - Requires authentication
- `GET /api/test/manager` - Requires HALL_MANAGER role
- `GET /api/test/admin` - Requires ADMIN role

#### Using JWT Token
```http
GET /api/test/admin
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## Configuration

### Application Properties
```properties
# JWT Configuration
jwt.secret=bXlTZWNyZXRLZXlGb3JITVNKV1RBdXRoZW50aWNhdGlvbjEyMzQ1Njc4OTA=
jwt.expirationMs=86400000

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/HMS
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Security Configuration
- **Session Management**: Stateless (JWT-based)
- **Password Encoding**: BCrypt
- **Token Validation**: Every request
- **CORS**: Enabled for all origins (configure for production)

## User Roles

### 1. ADMIN
- Full system access
- User management
- System configuration

### 2. HALL_MANAGER
- Hall-specific operations
- Student management within hall
- Fee management

### 3. STUDENT
- Personal profile access
- Fee viewing
- Basic operations

### 4. AUTHORITY
- Supervisory access
- Administrative oversight
- Policy enforcement

### 5. SUPERVISOR
- Monitoring and supervision
- Performance oversight
- Quality assurance

## Security Features

### 1. **Token Security**
- HMAC SHA-256 signature
- Configurable expiration
- Automatic validation on each request

### 2. **Password Security**
- BCrypt hashing with salt
- Automatic migration of existing passwords
- Strong password validation

### 3. **Request Security**
- CSRF protection disabled (suitable for stateless JWT)
- XSS protection via Spring Security
- Secure headers

### 4. **Error Handling**
- Proper HTTP status codes
- Secure error messages
- No information leakage

## Database Migration

The system includes automatic password migration:
- Detects plain text passwords
- Converts to BCrypt hashes
- Runs on application startup
- No data loss

## Frontend Integration

### React/Vue.js Example
```javascript
// Login function
const login = async (email, password) => {
    const response = await fetch('/api/auth/signin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
        // Store token
        localStorage.setItem('token', data.accessToken);
        localStorage.setItem('user', JSON.stringify(data));
    }
    
    return data;
};

// Authenticated request
const makeAuthenticatedRequest = async (url, options = {}) => {
    const token = localStorage.getItem('token');
    
    return fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
};
```

## Testing

### 1. **Postman Testing**
Import the following collection:

```json
{
    "info": {
        "name": "HMS JWT Authentication",
        "description": "JWT Authentication endpoints for HMS"
    },
    "item": [
        {
            "name": "Sign In",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"email\": \"admin@dormie.com\",\n    \"password\": \"admin123\"\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/api/auth/signin",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "auth", "signin"]
                }
            }
        }
    ]
}
```

### 2. **cURL Testing**
```bash
# Sign in
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@dormie.com", "password": "admin123"}'

# Access protected endpoint
curl -X GET http://localhost:8080/api/test/admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Production Recommendations

### 1. **Security Enhancements**
- Use environment variables for JWT secret
- Implement token refresh mechanism
- Add rate limiting
- Enable HTTPS only

### 2. **Configuration**
```properties
# Production JWT config
jwt.secret=${JWT_SECRET}
jwt.expirationMs=3600000  # 1 hour
jwt.refreshExpirationMs=86400000  # 24 hours

# CORS for production
cors.allowed-origins=https://yourdomain.com
```

### 3. **Monitoring**
- Log authentication attempts
- Monitor token usage
- Track failed login attempts
- Set up alerts for suspicious activity

## Troubleshooting

### Common Issues

1. **Token Expired**
   - Error: `JWT token is expired`
   - Solution: Implement token refresh or re-login

2. **Invalid Token**
   - Error: `Invalid JWT token`
   - Solution: Check token format and signature

3. **Access Denied**
   - Error: `Access is denied`
   - Solution: Verify user roles and permissions

4. **CORS Issues**
   - Error: `CORS policy`
   - Solution: Update CORS configuration

### Debug Mode
Enable debug logging:
```properties
logging.level.com.HMS.hms.Security=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Conclusion

This JWT implementation provides a robust, secure authentication system for the HMS application. It follows Spring Security best practices and provides a foundation for scaling the application with proper security measures.

Key benefits:
- ✅ Secure token-based authentication
- ✅ Role-based access control
- ✅ Password security with BCrypt
- ✅ Backward compatibility
- ✅ Production-ready configuration
- ✅ Comprehensive error handling
- ✅ Easy frontend integration
