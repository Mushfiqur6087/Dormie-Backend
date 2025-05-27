package com.HMS.hms.Payment.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.HMS.hms.Payment.SSLCommerz;

/**
 * Utility class for handling SSLCommerz payment transactions
 * Provides a cleaner interface for payment initiation with better error handling
 * and configuration management
 */
@Component
public class PaymentTransactionHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentTransactionHelper.class);
    
    @Autowired
    private SSLCommerzConfiguration sslCommerzConfig;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${app.base.url:http://localhost}")
    private String baseUrl;
    
    /**
     * Data Transfer Object for payment transaction details
     */
    public static class PaymentTransactionRequest {
        private String customerName;
        private String customerEmail;
        private String amount;
        private String productName;
        private String customerPhone;
        private String customerAddress;
        private String transactionId;
        
        // Constructor
        public PaymentTransactionRequest(String customerName, String customerEmail, String amount, String productName) {
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.amount = amount;
            this.productName = productName;
            this.customerPhone = "01XXXXXXXXX"; // Default phone
            this.customerAddress = "BUET Campus"; // Default address
            this.transactionId = "TXN-" + UUID.randomUUID();
        }
        
        // Getters and setters
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        
        public String getCustomerAddress() { return customerAddress; }
        public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    }
    
    /**
     * Initiates a payment transaction with SSLCommerz
     * @param request Payment transaction request containing customer and transaction details
     * @return PaymentTransactionResponse containing the result of the operation
     */
    public PaymentTransactionResponse initiatePaymentTransactionWithResponse(PaymentTransactionRequest request) {
        try {
            logger.info("Initiating payment transaction for customer: {}, amount: {}", 
                       request.getCustomerName(), request.getAmount());
            
            // Build payment parameters
            Map<String, String> paymentParams = buildPaymentParameters(request);
            
            // Initialize SSLCommerz
            SSLCommerz sslCommerz = new SSLCommerz(
                sslCommerzConfig.getStore().getId(), 
                sslCommerzConfig.getStore().getPassword(), 
                sslCommerzConfig.getSandbox().isEnabled()
            );
            
            // Initiate transaction
            String response = sslCommerz.initiateTransaction(paymentParams, false);
            
            // Parse the response
            com.HMS.hms.Payment.parametermappings.SSLCommerzInitResponse initResponse = 
                com.HMS.hms.Payment.Utility.Util.extractInitResponse(response);
            
            if ("SUCCESS".equalsIgnoreCase(initResponse.getStatus())) {
                logger.info("Payment transaction initiated successfully for transaction ID: {}", 
                           request.getTransactionId());
                
                PaymentTransactionResponse result = PaymentTransactionResponse.success(
                    initResponse.getGatewayPageURL(), request.getTransactionId());
                result.setGatewayResponse(response);
                return result;
            } else {
                logger.warn("Payment transaction initiation failed for transaction ID: {} - Reason: {}", 
                           request.getTransactionId(), initResponse.getFailedreason());
                return PaymentTransactionResponse.failure(
                    initResponse.getFailedreason(), response);
            }
            
        } catch (Exception e) {
            logger.error("Failed to initiate payment transaction for customer: {} - Error: {}", 
                        request.getCustomerName(), e.getMessage(), e);
            return PaymentTransactionResponse.failure(
                "Payment transaction initiation failed: " + e.getMessage());
        }
    }
    
    /**
     * Initiates a payment transaction with SSLCommerz (legacy method)
     * @param request Payment transaction request containing customer and transaction details
     * @return Response string from SSLCommerz API
     * @throws Exception if transaction initiation fails
     * @deprecated Use initiatePaymentTransactionWithResponse() for better error handling
     */
    @Deprecated
    public String initiatePaymentTransaction(PaymentTransactionRequest request) throws Exception {
        try {
            logger.info("Initiating payment transaction for customer: {}, amount: {}", 
                       request.getCustomerName(), request.getAmount());
            
            // Build payment parameters
            Map<String, String> paymentParams = buildPaymentParameters(request);
            
            // Initialize SSLCommerz
            SSLCommerz sslCommerz = new SSLCommerz(
                sslCommerzConfig.getStore().getId(), 
                sslCommerzConfig.getStore().getPassword(), 
                sslCommerzConfig.getSandbox().isEnabled()
            );
            
            // Initiate transaction
            String response = sslCommerz.initiateTransaction(paymentParams, false);
            
            logger.info("Payment transaction initiated successfully for transaction ID: {}", 
                       request.getTransactionId());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to initiate payment transaction for customer: {} - Error: {}", 
                        request.getCustomerName(), e.getMessage(), e);
            throw new Exception("Payment transaction initiation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Builds the payment parameters map required by SSLCommerz
     * @param request Payment transaction request
     * @return Map containing all required payment parameters
     */
    private Map<String, String> buildPaymentParameters(PaymentTransactionRequest request) {
        Map<String, String> params = new HashMap<>();
        
        // Transaction details
        params.put("total_amount", request.getAmount());
        params.put("tran_id", request.getTransactionId());
        params.put("currency", "BDT");
        
        // Product information
        params.put("product_name", request.getProductName());
        params.put("product_category", "Fee");
        params.put("product_profile", "general");
        
        // Customer information
        params.put("cus_name", request.getCustomerName());
        params.put("cus_email", request.getCustomerEmail());
        params.put("cus_add1", request.getCustomerAddress());
        params.put("cus_city", "Dhaka");
        params.put("cus_postcode", "1000");
        params.put("cus_country", "Bangladesh");
        params.put("cus_phone", request.getCustomerPhone());
        
        // URL configuration
        String fullBaseUrl = baseUrl + ":" + serverPort;
        params.put("success_url", fullBaseUrl + sslCommerzConfig.getUrls().getSuccess());
        params.put("fail_url", fullBaseUrl + sslCommerzConfig.getUrls().getFail());
        params.put("cancel_url", fullBaseUrl + sslCommerzConfig.getUrls().getCancel());
        
        // Additional settings
        params.put("shipping_method", "NO");
        params.put("num_of_item", "1");
        params.put("value_a", request.getCustomerEmail()); // Store customer email for reference
        params.put("value_b", request.getProductName()); // Store product name for reference
        
        logger.debug("Built payment parameters for transaction: {}", request.getTransactionId());
        
        return params;
    }
    
    /**
     * Creates a payment transaction request with the provided details
     * @param customerName Customer's name
     * @param customerEmail Customer's email
     * @param amount Transaction amount
     * @param productName Product/service name
     * @return PaymentTransactionRequest object
     */
    public PaymentTransactionRequest createPaymentRequest(String customerName, String customerEmail, 
                                                         String amount, String productName) {
        return new PaymentTransactionRequest(customerName, customerEmail, amount, productName);
    }
    
    /**
     * Validates the payment request parameters
     * @param request Payment transaction request
     * @throws IllegalArgumentException if validation fails
     */
    public void validatePaymentRequest(PaymentTransactionRequest request) throws IllegalArgumentException {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }
        
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        
        if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }
        
        if (request.getAmount() == null || request.getAmount().trim().isEmpty()) {
            throw new IllegalArgumentException("Amount is required");
        }
        
        try {
            double amount = Double.parseDouble(request.getAmount());
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format");
        }
        
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        
        logger.debug("Payment request validation passed for transaction: {}", request.getTransactionId());
    }
    
    /**
     * Gets the configured store ID
     * @return Store ID
     */
    public String getStoreId() {
        return sslCommerzConfig.getStore().getId();
    }
    
    /**
     * Checks if sandbox mode is enabled
     * @return true if sandbox mode is enabled
     */
    public boolean isSandboxMode() {
        return sslCommerzConfig.getSandbox().isEnabled();
    }
}
