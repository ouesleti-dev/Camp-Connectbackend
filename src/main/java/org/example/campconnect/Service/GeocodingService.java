package org.example.campconnect.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeocodingService {

    private final RestTemplate restTemplate;
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    public GeocodingService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    public double[] geocode(String address) {
        String cleaned = cleanAddress(address);
        log.info("Original: '{}' -> Cleaned: '{}'", address, cleaned);

        double[] result = tryGeocode(cleaned);
        if (result != null) return result;

        String[] parts = cleaned.split(",");
        for (int i = 1; i < parts.length; i++) {
            StringBuilder simplified = new StringBuilder();
            for (int j = i; j < parts.length; j++) {
                if (simplified.length() > 0) simplified.append(", ");
                simplified.append(parts[j].trim());
            }
            String simpler = simplified.toString();
            log.info("Retrying with simplified address: '{}'", simpler);

            try { Thread.sleep(1100); } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }

            result = tryGeocode(simpler);
            if (result != null) return result;
        }

        return null;
    }

    private String cleanAddress(String address) {
        String cleaned = address;
        cleaned = cleaned.replaceAll("(?i)gouvernorat\\s+(de\\s+)?", "");
        cleaned = cleaned.replaceAll("(?i)\\bTunisie\\b", "Tunisia");
        cleaned = cleaned.replaceAll("\\s{2,}", " ").trim();
        return cleaned;
    }

    private double[] tryGeocode(String address) {
        try {
            String encoded = java.net.URLEncoder.encode(address, "UTF-8");
            String url = NOMINATIM_URL + "?q=" + encoded
                    + "&format=json&limit=1&addressdetails=0&countrycodes=tn";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "CampConnect/1.0 (camping marketplace)");
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Accept-Language", "en");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            log.info("Geocoding request: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            log.info("Geocoding response status: {}, body length: {}",
                    response.getStatusCode(),
                    response.getBody() != null ? response.getBody().length() : 0);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                double lat = first.get("lat").asDouble();
                double lon = first.get("lon").asDouble();
                log.info("Geocoded '{}' -> [{}, {}]", address, lat, lon);
                return new double[]{lat, lon};
            } else {
                log.warn("Nominatim returned empty results for: '{}'", address);
            }
        } catch (Exception e) {
            log.error("Geocoding failed for '{}' — {}", address, e.getMessage());
        }
        return null;
    }
}
