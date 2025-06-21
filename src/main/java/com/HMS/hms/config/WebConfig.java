// In your main application class (e.g., HmsApplication.java) or a separate @Configuration class
package com.HMS.hms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean; // <--- Import @Bean
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate; // <--- Import RestTemplate
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // --- ADD THIS BEAN DEFINITION FOR RESTTEMPLATE ---
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    // --- END ADDITION ---

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // This is the URL path your frontend will use (e.g., http://localhost:8080/uploads/myimage.jpg)
                .addResourceLocations("file:" + uploadDir + "/"); // This is the actual file system path
    }
}