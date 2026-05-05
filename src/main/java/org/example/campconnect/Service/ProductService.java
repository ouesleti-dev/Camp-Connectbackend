package org.example.campconnect.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Product;
import org.example.campconnect.Entity.ProductStatus;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.OrderLineRepository;
import org.example.campconnect.Repository.ProductRepository;
import org.example.campconnect.Repository.ProductReviewRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ProductRequestDTO;
import org.example.campconnect.dto.ProductResponseDTO;
import org.example.campconnect.dto.ProductSalesStatsDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductReviewRepository reviewRepository;
    private final OrderLineRepository orderLineRepository;
    @Override
    public ProductResponseDTO addProduct(ProductRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = Product.builder()
                .nameProduct(dto.getNameProduct())
                .descriptionProduct(dto.getDescriptionProduct())
                .priceProduct(dto.getPriceProduct())
                .quantityProduct(dto.getQuantityProduct())
                .photoProduct(dto.getPhotoProduct())
                .locationProduct(dto.getLocationProduct())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .category(dto.getCategory())
                .productState(dto.getProductState())
                .productStatus(ProductStatus.PENDING)
                .addedDate(new Date())
                .user(user)
                .build();

        return toDTO(productRepository.save(product));
    }


    @Override
    public List<ProductResponseDTO> getApprovedProducts() {
        return productRepository.findByProductStatus(ProductStatus.APPROVED)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Admin: all products
    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Admin: pending products
    @Override
    public List<ProductResponseDTO> getPendingProducts() {
        return productRepository.findByProductStatus(ProductStatus.PENDING)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // My products (seller)
    @Override
    public List<ProductResponseDTO> getMyProducts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return productRepository.findByUserIdUser(user.getIdUser())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
    @Override
    public ProductResponseDTO getProductById(Long id) {
        return toDTO(productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setNameProduct(dto.getNameProduct());
        product.setDescriptionProduct(dto.getDescriptionProduct());
        product.setPriceProduct(dto.getPriceProduct());
        product.setQuantityProduct(dto.getQuantityProduct());
        product.setPhotoProduct(dto.getPhotoProduct());
        product.setLocationProduct(dto.getLocationProduct());
        product.setLatitude(dto.getLatitude());
        product.setLongitude(dto.getLongitude());
        product.setCategory(dto.getCategory());
        product.setProductState(dto.getProductState());
        product.setProductStatus(ProductStatus.PENDING); // re-submit for approval after edit
        return toDTO(productRepository.save(product));
    }
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        orderLineRepository.deleteFromJoinTableByProductId(id);
        orderLineRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    // Admin: approve product
    @Override
    public ProductResponseDTO approveProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setProductStatus(ProductStatus.APPROVED);
        return toDTO(productRepository.save(product));
    }

    // Admin: reject product
    @Override
    public ProductResponseDTO rejectProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setProductStatus(ProductStatus.REJECTED);
        return toDTO(productRepository.save(product));
    }
    @Override
    public List<ProductResponseDTO> getProductsNearby(Double lat, Double lng, Double radiusKm) {
        return productRepository.findByProductStatus(ProductStatus.APPROVED)
                .stream()
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .filter(p -> haversineKm(lat, lng, p.getLatitude(), p.getLongitude()) <= radiusKm)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    @Override
    public List<ProductSalesStatsDTO> getProductSalesStats() {
        return productRepository.findProductSalesStats(ProductStatus.APPROVED)
                .stream()
                .map(row -> ProductSalesStatsDTO.builder()
                        .idProduct(((Number) row[0]).longValue())
                        .nameProduct((String) row[1])
                        .category(row[2] != null ? row[2].toString() : null)
                        .priceProduct(((Number) row[3]).doubleValue())
                        .productState(row[4] != null ? row[4].toString() : null)
                        .productStatus(row[5] != null ? row[5].toString() : null)
                        .totalQuantitySold(((Number) row[6]).longValue())
                        .totalRevenue(((Number) row[7]).doubleValue())
                        .distinctBuyers(((Number) row[8]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    public ProductResponseDTO toDTO(Product p) {
        Double avg = reviewRepository.findAverageRatingByProductId(p.getIdProduct());
        Long count = reviewRepository.countByProductId(p.getIdProduct());
        return ProductResponseDTO.builder()
                .idProduct(p.getIdProduct())
                .nameProduct(p.getNameProduct())
                .descriptionProduct(p.getDescriptionProduct())
                .priceProduct(p.getPriceProduct())
                .quantityProduct(p.getQuantityProduct())
                .photoProduct(p.getPhotoProduct())
                .locationProduct(p.getLocationProduct())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .category(p.getCategory())
                .productState(p.getProductState())
                .productStatus(p.getProductStatus())
                .addedDate(p.getAddedDate())
                .userId(p.getUser() != null ? p.getUser().getIdUser() : null)
                .sellerName(p.getUser() != null ? p.getUser().getFirstName() + " " + p.getUser().getLastName() : null)
                .averageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null)
                .reviewCount(count != null ? count : 0L)
                .build();
    }
}
