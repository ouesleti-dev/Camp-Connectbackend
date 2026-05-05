package org.example.campconnect.Service;

import org.example.campconnect.dto.ProductReviewRequest;
import org.example.campconnect.dto.ProductReviewResponseDTO;

import java.util.List;

public interface IProductReviewService {
    ProductReviewResponseDTO addReview(ProductReviewRequest request);
    List<ProductReviewResponseDTO> getReviewsByProduct(Long productId);
    boolean hasUserReviewedProduct(Long userId, Long productId);
}