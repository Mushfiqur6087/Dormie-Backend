package com.HMS.hms.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Map;

@Service
public class SSLCommerzValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SSLCommerzValidationService.class);
    
    @Value("${sslcommerz.store.id:abc682f4e02dae8b}")
    private String storeId;
    
    @Value("${sslcommerz.store.password:abc682f4e02dae8b@ssl}")
    private String storePassword;
    
    @Value("${sslcommerz.sandbox.url:https://sandbox.sslcommerz.com}")
    private String baseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Validates a transaction with SSLCommerz using their validation API
     * This provides an additional security layer by confirming transaction details with SSLCommerz
     */
    public boolean validateTransaction(String valId, String tranId, String amount) {
        try {
            String validationUrl = baseUrl + "/validator/api/validationserverAPI.php";
            
            String url = UriComponentsBuilder.fromHttpUrl(validationUrl)
                    .queryParam("val_id", valId)
                    .queryParam("store_id", storeId)
                    .queryParam("store_passwd", storePassword)
                    .queryParam("format", "json")
                    .toUriString();
            
            logger.info("Validating transaction with SSLCommerz: val_id={}, tran_id={}", valId, tranId);
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null && response.contains("\"status\":\"VALID\"")) {
                // Additional checks can be added here to validate amount, transaction ID, etc.
                logger.info("Transaction validation successful for val_id: {}", valId);
                return true;
            } else {
                logger.warn("Transaction validation failed for val_id: {}. Response: {}", valId, response);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error validating transaction with SSLCommerz for val_id: " + valId, e);
            return false;
        }
    }
    
    /**
     * Validates callback parameters against expected values
     */
    public boolean validateCallbackParameters(Map<String, String> params) {
        try {
            String status = params.get("status");
            String tranId = params.get("tran_id");
            String amount = params.get("amount");
            
            // Basic validation
            if (status == null || tranId == null || amount == null) {
                logger.warn("Missing required parameters in callback");
                return false;
            }
            
            // Validate transaction ID format (should start with TXN-)
            if (!tranId.startsWith("TXN-")) {
                logger.warn("Invalid transaction ID format: {}", tranId);
                return false;
            }
            
            // Validate amount (should be positive number)
            try {
                double amountValue = Double.parseDouble(amount);
                if (amountValue <= 0) {
                    logger.warn("Invalid amount: {}", amount);
                    return false;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid amount format: {}", amount);
                return false;
            }
            
            // Validate status
            if (!Arrays.asList("VALID", "FAILED", "CANCELLED").contains(status)) {
                logger.warn("Invalid status: {}", status);
                return false;
            }
            
            logger.info("Callback parameters validation passed for transaction: {}", tranId);
            return true;
            
        } catch (Exception e) {
            logger.error("Error validating callback parameters", e);
            return false;
        }
    }
}
