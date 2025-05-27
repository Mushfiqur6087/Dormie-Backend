package com.HMS.hms.Payment.Utility;

/**
 * Data Transfer Object for payment transaction responses
 * Provides a structured way to handle payment responses
 */
public class PaymentTransactionResponse {
    
    private boolean success;
    private String redirectUrl;
    private String errorMessage;
    private String transactionId;
    private String gatewayResponse;
    
    // Private constructor to enforce builder pattern
    private PaymentTransactionResponse() {}
    
    /**
     * Creates a successful payment response
     * @param redirectUrl The URL to redirect the user to for payment
     * @param transactionId The transaction ID generated
     * @return PaymentTransactionResponse for success case
     */
    public static PaymentTransactionResponse success(String redirectUrl, String transactionId) {
        PaymentTransactionResponse response = new PaymentTransactionResponse();
        response.success = true;
        response.redirectUrl = redirectUrl;
        response.transactionId = transactionId;
        return response;
    }
    
    /**
     * Creates a failed payment response
     * @param errorMessage The error message describing the failure
     * @return PaymentTransactionResponse for failure case
     */
    public static PaymentTransactionResponse failure(String errorMessage) {
        PaymentTransactionResponse response = new PaymentTransactionResponse();
        response.success = false;
        response.errorMessage = errorMessage;
        return response;
    }
    
    /**
     * Creates a failed payment response with gateway response
     * @param errorMessage The error message describing the failure
     * @param gatewayResponse The raw response from the payment gateway
     * @return PaymentTransactionResponse for failure case
     */
    public static PaymentTransactionResponse failure(String errorMessage, String gatewayResponse) {
        PaymentTransactionResponse response = new PaymentTransactionResponse();
        response.success = false;
        response.errorMessage = errorMessage;
        response.gatewayResponse = gatewayResponse;
        return response;
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getGatewayResponse() {
        return gatewayResponse;
    }
    
    
    void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
}
