package com.HMS.hms.Payment.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.HMS.hms.Payment.parametermappings.SSLCommerzInitResponse;
import com.HMS.hms.Payment.parametermappings.SSLCommerzValidatorResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

    public static SSLCommerzInitResponse extractInitResponse(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SSLCommerzInitResponse sslInitResponse = mapper.readValue(response, SSLCommerzInitResponse.class);
        System.out.println("SSLCommerzInitResponse: " + sslInitResponse);
        return sslInitResponse;
    }

    public static SSLCommerzValidatorResponse extractValidatorResponse(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SSLCommerzValidatorResponse sslValidatorResponse = mapper.readValue(response, SSLCommerzValidatorResponse.class);
        return sslValidatorResponse;
    }

    public static String getByOpeningJavaUrlConnection(String stringUrl) throws IOException {
        StringBuilder output = new StringBuilder();
        URL url = new URL(stringUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String outputLine;
            while ((outputLine = br.readLine()) != null) {
                output.append(outputLine);
            }
        }
        return output.toString();
    }
    // Add this method to Util.java
    public static String postToUrl(String urlString, String urlParameters) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = urlParameters.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}
