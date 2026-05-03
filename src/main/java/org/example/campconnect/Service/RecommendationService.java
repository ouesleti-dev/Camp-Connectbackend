package org.example.campconnect.Service;

import org.example.campconnect.dto.ProductResponseDTO;
import org.example.campconnect.dto.RecommendationDTO;
import org.example.campconnect.Repository.ProductRepository;
import org.example.campconnect.Entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Value("${recommendation.api.url:http://localhost:8000}")
    private String aiApiUrl;

    public RecommendationService(ProductRepository productRepository,
                                  ProductService productService) {
        this.restTemplate     = new RestTemplate();
        this.productRepository = productRepository;
        this.productService    = productService;
    }

    public List<ProductResponseDTO> getRecommendations(Long productId, int topN) {
        String url = aiApiUrl + "/recommend/" + productId + "?top_n=" + topN;

        try {
            RecommendationDTO response = restTemplate.getForObject(url, RecommendationDTO.class);

            if (response == null || response.getRecommendations() == null) {
                return new ArrayList<>();
            }

            // Fetch full product details for each recommended ID
            List<Long> recommendedIds = response.getRecommendations()
                    .stream()
                    .map(r -> (long) r.getProductId())
                    .collect(Collectors.toList());

            List<ProductResponseDTO> result = new ArrayList<>();
            for (Long rid : recommendedIds) {
                Optional<Product> product = productRepository.findById(rid);
                product.ifPresent(p -> result.add(productService.toDTO(p)));
            }

            return result;

        } catch (Exception e) {
            // AI service unavailable — return empty list gracefully
            System.err.println("Recommendation service unavailable: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
