package com.HMS.hms.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.Payment.Utility.ParameterBuilder;
import com.HMS.hms.Payment.Utility.Util;
import com.HMS.hms.Payment.parametermappings.SSLCommerzInitResponse;
import com.HMS.hms.Service.SSLCommerzValidationService;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private SSLCommerzValidationService validationService;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment() throws IOException, UnsupportedEncodingException {
        Map<String, String> params = ParameterBuilder.constructRequestParameters();
        String paramString = ParameterBuilder.getParamsString(params, true);
        String sslcommerzUrl = "https://sandbox.sslcommerz.com/gwprocess/v4/api.php";
        String response = Util.postToUrl(sslcommerzUrl, paramString);

        SSLCommerzInitResponse initResponse = Util.extractInitResponse(response);

        if ("SUCCESS".equalsIgnoreCase(initResponse.getStatus())) {
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .location(URI.create(initResponse.getGatewayPageURL()))
//                    .build();
            return ResponseEntity.ok(Map.of("redirect_url", initResponse.getGatewayPageURL()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(initResponse.getFailedreason());
        }
    }

    @PostMapping("/ssl-success-page")
    public ResponseEntity<?> handleSuccessPost(@RequestParam Map<String, String> params) {
        logger.info("Payment success callback received with params: {}", params);
        
        // Validate callback parameters
        if (!validationService.validateCallbackParameters(params)) {
            logger.warn("Invalid callback parameters received");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid callback parameters");
        }
        
        String tranId = params.getOrDefault("tran_id", "unknown");
        String valId = params.getOrDefault("val_id", "unknown");
        String amount = params.getOrDefault("amount", "0");
        String status = params.getOrDefault("status", "unknown");
        
        // Additional validation with SSLCommerz server
        if ("VALID".equals(status)) {
            if (!validationService.validateTransaction(valId, tranId, amount)) {
                logger.warn("Transaction validation failed for val_id: {}, tran_id: {}", valId, tranId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Transaction validation failed");
            }
            logger.info("Payment validation successful for transaction: {}", tranId);
        }

        // Redirect to static page with query params
        String redirectUrl = "/payment-success.html?tran_id=" + tranId + "&val_id=" + valId + "&status=" + status;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    // Optional GET endpoint for testing in browser
    @GetMapping("/ssl-success-page")
    @ResponseBody
    public String paymentSuccessGet() {
        return "Payment Success (GET)";
    }

    //Payment Failed
    @PostMapping("/ssl-fail-page")
    @ResponseBody
    public String paymentFailPost(@RequestParam Map<String, String> allParams) {
        logger.info("Payment failed callback received: {}", allParams);
        
        // Validate callback parameters
        if (!validationService.validateCallbackParameters(allParams)) {
            logger.warn("Invalid callback parameters received for failed payment");
            return "Invalid callback parameters";
        }
        
        return "Payment Failed<br><br>" + allParams.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .reduce("", (a, b) -> a + "<br>" + b);
    }

    //Payment Cancelled
    @PostMapping("/ssl-cancel-page")
    @ResponseBody
    public String paymentCancelPost(@RequestParam Map<String, String> allParams) {
        logger.info("Payment cancelled callback received: {}", allParams);
        
        // Validate callback parameters
        if (!validationService.validateCallbackParameters(allParams)) {
            logger.warn("Invalid callback parameters received for cancelled payment");
            return "Invalid callback parameters";
        }
        
        return "Payment Cancelled<br><br>" + allParams.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .reduce("", (a, b) -> a + "<br>" + b);
    }
}
