package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Product;
import org.example.campconnect.Entity.ProductReview;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.ProductRepository;
import org.example.campconnect.Repository.ProductReviewRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ProductReviewRequest;
import org.example.campconnect.dto.ProductReviewResponseDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewService implements IProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ProductReviewResponseDTO addReview(ProductReviewRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (reviewRepository.existsByUserIdUserAndProductIdProduct(user.getIdUser(), product.getIdProduct())) {
            throw new RuntimeException("You have already reviewed this product");
        }

        ProductReview review = ProductReview.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewDate(new Date())
                .user(user)
                .product(product)
                .build();

        return toDTO(reviewRepository.save(review));
    }

    @Override
    public List<ProductReviewResponseDTO> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdProduct(productId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdUserAndProductIdProduct(userId, productId);
    }

    private ProductReviewResponseDTO toDTO(ProductReview r) {
        return ProductReviewResponseDTO.builder()
                .idProductReview(r.getIdProductReview())
                .rating(r.getRating())
                .comment(r.getComment())
                .reviewDate(r.getReviewDate())
                .reviewerId(r.getUser().getIdUser())
                .reviewerName(r.getUser().getFirstName() + " " + r.getUser().getLastName())
                .build();
    }
}