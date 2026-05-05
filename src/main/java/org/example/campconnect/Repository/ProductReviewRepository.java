package org.example.campconnect.Repository;

import org.example.campconnect.Entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProductIdProduct(Long productId);
    boolean existsByUserIdUserAndProductIdProduct(Long userId, Long productId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.idProduct = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.product.idProduct = :productId")
    Long countByProductId(@Param("productId") Long productId);
}