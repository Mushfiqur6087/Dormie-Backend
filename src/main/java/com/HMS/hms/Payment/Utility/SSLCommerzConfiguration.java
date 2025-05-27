package com.HMS.hms.Payment.Utility;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for SSLCommerz payment gateway
 * This allows for better configuration management and environment-specific settings
 */
@Configuration
@ConfigurationProperties(prefix = "sslcommerz")
public class SSLCommerzConfiguration {
    
    private Store store = new Store();
    private Sandbox sandbox = new Sandbox();
    private Urls urls = new Urls();
    
    public static class Store {
        private String id = "abc682f4e02dae8b";
        private String password = "abc682f4e02dae8b@ssl";
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class Sandbox {
        private boolean enabled = true;
        private String baseUrl = "https://sandbox.sslcommerz.com";
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }
    
    public static class Urls {
        private String success = "/payment/ssl-success-page";
        private String fail = "/payment/ssl-fail-page";
        private String cancel = "/payment/ssl-cancel-page";
        
        public String getSuccess() { return success; }
        public void setSuccess(String success) { this.success = success; }
        
        public String getFail() { return fail; }
        public void setFail(String fail) { this.fail = fail; }
        
        public String getCancel() { return cancel; }
        public void setCancel(String cancel) { this.cancel = cancel; }
    }
    
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
    
    public Sandbox getSandbox() { return sandbox; }
    public void setSandbox(Sandbox sandbox) { this.sandbox = sandbox; }
    
    public Urls getUrls() { return urls; }
    public void setUrls(Urls urls) { this.urls = urls; }
}
