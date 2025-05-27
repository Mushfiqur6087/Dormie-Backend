package com.HMS.hms.Payment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class initiates a transaction request to SSL Commerz
 * required parameters to hit SSL Commerz payment page are constructed in a Map of String as key value pair
 * Its method initTrnxnRequest returns JSON list or String with Session key which then used to select payment option
 */
public class TransactionInitiator {
    private static final Logger logger = LoggerFactory.getLogger(TransactionInitiator.class);
    
    public String initTrnxnRequest() {
        String response = "";
        try {
            /**
             * All parameters in payment order should be constructed in this follwing postData Map
             * keep an eye on success fail url correctly.
             * insert your success and fail URL correctly in this Map
             */
            Map<String, String> postData = constructDefaultRequestParameters();
            /**
             * Provide your SSL Commerz store Id and Password by this following constructor.
             * If Test Mode then insert true and false otherwise.
             */
            SSLCommerz sslcz = new SSLCommerz("abc682f4e02dae8b", "abc682f4e02dae8b@ssl", true);

            /**
             * If user want to get Gate way list then pass isGetGatewayList parameter as true
             * If user want to get URL as returned response, pass false.
             */
            response = sslcz.initiateTransaction(postData, false);
            return response;
        } catch (Exception e) {
            logger.error("Error initializing transaction: {}", e.getMessage(), e);
        }
        return response;
    }
    
    /**
     * Constructs default request parameters for SSL Commerz transaction
     * @return Map containing default request parameters
     */
    private Map<String, String> constructDefaultRequestParameters() {
        // CREATING LIST OF POST DATA
        String baseUrl = "http://localhost:8080/";
        Map<String, String> postData = new HashMap<>();
        postData.put("store_id", "abc682f4e02dae8b"); // Store ID
        postData.put("store_passwd", "abc682f4e02dae8b@ssl"); // Store password
        postData.put("total_amount", "150.00"); // Default amount
        String uniqueTranId = "TXN-" + UUID.randomUUID();
        postData.put("tran_id", uniqueTranId); // Use unique tran_id for each API call
        postData.put("success_url", baseUrl + "payment/ssl-success-page");
        postData.put("fail_url", "https://sandbox.sslcommerz.com/developer/fail.php");
        postData.put("cancel_url", "https://sandbox.sslcommerz.com/developer/cancel.php");
        postData.put("cus_name", "Dormie User");
        postData.put("cus_email", "user@example.com");
        postData.put("cus_add1", "BUET Campus");
        postData.put("cus_city", "Dhaka");
        postData.put("cus_postcode", "1000");
        postData.put("cus_country", "Bangladesh");
        postData.put("cus_phone", "0111111111");
        postData.put("shipping_method", "NO");
        postData.put("product_name", "Dormitory Fees");
        postData.put("product_category", "Education");
        postData.put("product_profile", "general");
        return postData;
    }
}
