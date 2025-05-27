package com.HMS.hms.Security;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SSLCommerzSecurityFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(SSLCommerzSecurityFilter.class);
    
    @Value("${sslcommerz.store.password:abc682f4e02dae8b@ssl}")
    private String storePassword;
    
    @Value("${sslcommerz.environment:sandbox}")
    private String environment;
    
    @Value("${sslcommerz.security.disable-ip-validation:false}")
    private boolean disableIpValidation;
    
    // SSLCommerz server IPs (from documentation)
    private static final List<String> SSLCOMMERZ_IPS = Arrays.asList(
        "103.26.139.87",   // Sandbox
        "103.26.139.81",   // Production Primary
        "103.26.139.148",  // Production Primary
        "103.132.153.81",  // Production Secondary
        "103.132.153.148", // Production Secondary
        "127.0.0.1",       // For local testing
        "::1",             // IPv6 localhost for testing
        "0:0:0:0:0:0:0:1", // IPv6 localhost alternative
        "192.168.1.1",     // Common local network range (adjust as needed)
        "10.0.0.1",        // Common local network range
        "172.16.0.1"       // Common local network range
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestUri = httpRequest.getRequestURI();
        
        // Only apply security to payment callback endpoints
        if (isPaymentCallback(requestUri)) {
            logger.info("Applying SSLCommerz security filter for: {}", requestUri);
            
            // Validate IP address
            if (!isValidSourceIP(httpRequest)) {
                logger.warn("Unauthorized IP attempted to access payment callback: {}", getClientIP(httpRequest));
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("Access denied: Invalid source IP");
                return;
            }
            
            // For POST requests (IPN callbacks), validate signature
            if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
                if (!validateSignature(httpRequest)) {
                    logger.warn("Invalid signature for payment callback from IP: {}", getClientIP(httpRequest));
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.getWriter().write("Access denied: Invalid signature");
                    return;
                }
            }
            
            logger.info("SSLCommerz security validation passed for: {}", requestUri);
        }
        
        // Continue with the filter chain
        chain.doFilter(request, response);
    }
    
    private boolean isPaymentCallback(String requestUri) {
        return requestUri.startsWith("/payment/ssl-");
    }
    
    private boolean isValidSourceIP(HttpServletRequest request) {
        // If IP validation is disabled via configuration
        if (disableIpValidation) {
            logger.debug("IP validation is disabled via configuration");
            return true;
        }
        
        String clientIP = getClientIP(request);
        
        // In sandbox/testing environment, be more lenient with IP validation
        if ("sandbox".equalsIgnoreCase(environment)) {
            // Allow localhost variations and private IPs for testing
            if (clientIP.equals("127.0.0.1") || 
                clientIP.equals("::1") || 
                clientIP.equals("0:0:0:0:0:0:0:1") ||
                clientIP.startsWith("192.168.") ||
                clientIP.startsWith("10.") ||
                clientIP.startsWith("172.16.") ||
                clientIP.startsWith("172.17.") ||
                clientIP.startsWith("172.18.") ||
                clientIP.startsWith("172.19.") ||
                clientIP.startsWith("172.20.") ||
                clientIP.startsWith("172.21.") ||
                clientIP.startsWith("172.22.") ||
                clientIP.startsWith("172.23.") ||
                clientIP.startsWith("172.24.") ||
                clientIP.startsWith("172.25.") ||
                clientIP.startsWith("172.26.") ||
                clientIP.startsWith("172.27.") ||
                clientIP.startsWith("172.28.") ||
                clientIP.startsWith("172.29.") ||
                clientIP.startsWith("172.30.") ||
                clientIP.startsWith("172.31.")) {
                logger.debug("Allowing local/private IP in sandbox mode: {}", clientIP);
                return true;
            }
        }
        
        boolean isValid = SSLCOMMERZ_IPS.contains(clientIP);
        logger.debug("Validating IP: {} - Valid: {}", clientIP, isValid);
        return isValid;
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    private boolean validateSignature(HttpServletRequest request) {
        try {
            // Get verification parameters from SSLCommerz
            String verifySign = request.getParameter("verify_sign");
            String verifyKey = request.getParameter("verify_key");
            
            if (verifySign == null || verifyKey == null) {
                logger.warn("Missing verify_sign or verify_key in payment callback");
                return false;
            }
            
            // Build the signature validation string according to SSLCommerz specification
            StringBuilder signatureData = new StringBuilder();
            
            // Split verify_key to get the parameter names that should be included in signature
            String[] verifyParams = verifyKey.split(",");
            
            for (String param : verifyParams) {
                String value = request.getParameter(param.trim());
                if (value != null) {
                    signatureData.append(value);
                }
            }
            
            // Append store password for signature generation
            signatureData.append(storePassword);
            
            // Generate MD5 hash
            String calculatedSign = generateMD5(signatureData.toString());
            
            boolean isValid = verifySign.equalsIgnoreCase(calculatedSign);
            logger.debug("Signature validation - Expected: {}, Calculated: {}, Valid: {}", 
                        verifySign, calculatedSign, isValid);
            
            return isValid;
            
        } catch (SecurityException | IllegalArgumentException e) {
            logger.error("Error validating SSLCommerz signature", e);
            return false;
        }
    }
    
    private String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            logger.error("Error generating MD5 hash", e);
            return "";
        }
    }
}
