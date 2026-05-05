package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class RecommendationDTO {

    @JsonProperty("source_product_id")
    private int sourceProductId;

    private List<RecommendedProduct> recommendations;

    @Data
    public static class RecommendedProduct {
        @JsonProperty("product_id")
        private int productId;
        private double score;
    }
}
