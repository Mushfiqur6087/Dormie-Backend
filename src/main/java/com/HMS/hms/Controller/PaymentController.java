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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.Security.UserDetailsImpl;
import com.HMS.hms.Service.PaymentService;
@RestController
@RequestMapping("/payment")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> initiatePayment() throws IOException, UnsupportedEncodingException {
        // Get authenticated user information from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not authenticated");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        String email = userDetails.getEmail();
        String username = userDetails.getUsername();
        
        // Delegate business logic to service layer
        PaymentService.PaymentInitiationResult result = paymentService.initiatePayment(userId, email, username);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                "redirect_url", result.getRedirectUrl(),
                "transaction_id", result.getTransactionId()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Payment initiation failed",
                        "message", result.getErrorMessage()
                    ));
        }
    }

    @PostMapping("/ssl-success-page")
    public ResponseEntity<?> handleSuccessPost(@RequestParam Map<String, String> params) {
        PaymentService.PaymentCallbackResult result = paymentService.processSuccessCallback(params);
        
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(result.getRedirectUrl()))
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
        PaymentService.PaymentCallbackResult result = paymentService.processFailureCallback(allParams);
        return result.getMessage();
    }

    //Payment Cancelled
    @PostMapping("/ssl-cancel-page")
    @ResponseBody
    public String paymentCancelPost(@RequestParam Map<String, String> allParams) {
        PaymentService.PaymentCallbackResult result = paymentService.processCancellationCallback(allParams);
        return result.getMessage();
    }
}
