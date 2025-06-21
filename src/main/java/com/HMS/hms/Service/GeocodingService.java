package com.HMS.hms.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders; // Import HttpHeaders
import org.springframework.http.HttpMethod;  // Import HttpMethod
import org.springframework.http.HttpEntity; // Import HttpEntity
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Spring's HTTP client
import org.springframework.web.util.UriComponentsBuilder; // For safe URL building
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Map; // To parse JSON response into a Map

@Service
public class GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    @Value("${nominatim.base.url}")
    private String nominatimBaseUrl;

    @Value("${nominatim.user-agent}")
    private String userAgent; // Injected from application.properties

    @Value("${hall.postcode}")
    private String hallPostcode; // Injected from application.properties

    private final RestTemplate restTemplate; // Injected via constructor

    // Constructor for dependency injection of RestTemplate
    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Geocodes a postcode to retrieve its latitude and longitude using Nominatim.
     * @param postcode The postal code to geocode.
     * @return A double array [latitude, longitude], or null if geocoding fails.
     */
    public double[] getCoordinatesFromPostcode(String postcode) {
        try {
            // Build the URL with query parameters safely
            URI uri = UriComponentsBuilder.fromUriString(nominatimBaseUrl)
                    .queryParam("postalcode", postcode)
                    .queryParam("countrycodes", "BD") // Filter for Bangladesh
                    .queryParam("format", "json")     // Request JSON format
                    .queryParam("limit", 1)           // Get only the best match
                    .build()
                    .toUri();

            // Create headers with User-Agent
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", userAgent);
            HttpEntity<String> entity = new HttpEntity<>(headers); // Entity with headers, no body

            // Make the HTTP GET request
            // Expecting a List of Maps, as Nominatim returns a JSON array of objects
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity, // Request entity with headers
                    List.class // Expecting a List (which Jackson will map from JSON array)
            );

            // Get the response body as a List of Maps
            // Each Map corresponds to a JSON object in the array (e.g., {"lat": "...", "lon": "..."})
            List<Map<String, String>> jsonResponse = (List<Map<String, String>>) responseEntity.getBody();

            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                Map<String, String> firstResult = jsonResponse.get(0);
                double lat = Double.parseDouble(firstResult.get("lat"));
                double lon = Double.parseDouble(firstResult.get("lon"));
                logger.info("Geocoded postcode {}: lat={}, lon={}", postcode, lat, lon);
                return new double[]{lat, lon};
            } else {
                logger.warn("Couldn't find coordinates for postcode: {}", postcode);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error during geocoding for postcode {}: {}", postcode, e.getMessage(), e);
            // In a production app, you might rethrow a custom exception or handle more gracefully
            return null;
        }
    }

    /**
     * Calculates the Haversine distance between two sets of coordinates.
     * @param lat1 Latitude of point 1.
     * @param lon1 Longitude of point 1.
     * @param lat2 Latitude of point 2.
     * @param lon2 Longitude of point 2.
     * @return Distance in kilometers.
     */
    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371; // Earth's radius in kilometers

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the distance from the hall's postcode to a given student postcode.
     * Uses the hallPostcode configured in application.properties.
     * @param studentPostcode The student's postcode.
     * @return Distance in kilometers, or null if geocoding fails for either postcode.
     */
    public Double calculateDistanceToHall(String studentPostcode) {
        double[] hallCoords = getCoordinatesFromPostcode(hallPostcode); // Get hall's coordinates
        double[] studentCoords = getCoordinatesFromPostcode(studentPostcode); // Get student's coordinates

        if (hallCoords != null && studentCoords != null) {
            return calculateHaversineDistance(
                    hallCoords[0], hallCoords[1],
                    studentCoords[0], studentCoords[1]
            );
        }
        return null; // Return null if coordinates couldn't be obtained for either location
    }
}