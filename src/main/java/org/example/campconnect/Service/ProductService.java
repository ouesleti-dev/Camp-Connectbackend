package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Product;
import org.example.campconnect.Entity.ProductStatus;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.ProductRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ProductRequestDTO;
import org.example.campconnect.dto.ProductResponseDTO;
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
                .category(dto.getCategory())
                .productState(dto.getProductState())
                .productStatus(ProductStatus.PENDING)
                .addedDate(new Date())
                .user(user)
                .build();

        return toDTO(productRepository.save(product));
    }

    // Public: only APPROVED products
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
        product.setCategory(dto.getCategory());
        product.setProductState(dto.getProductState());
        product.setProductStatus(ProductStatus.PENDING); // re-submit for approval after edit
        return toDTO(productRepository.save(product));
    }
    @Override
    public void deleteProduct(Long id) {
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

    private ProductResponseDTO toDTO(Product p) {
        return ProductResponseDTO.builder()
                .idProduct(p.getIdProduct())
                .nameProduct(p.getNameProduct())
                .descriptionProduct(p.getDescriptionProduct())
                .priceProduct(p.getPriceProduct())
                .quantityProduct(p.getQuantityProduct())
                .photoProduct(p.getPhotoProduct())
                .locationProduct(p.getLocationProduct())
                .category(p.getCategory())
                .productState(p.getProductState())
                .productStatus(p.getProductStatus())
                .addedDate(p.getAddedDate())
                .userId(p.getUser() != null ? p.getUser().getIdUser() : null)
                .sellerName(p.getUser() != null ? p.getUser().getFirstName() + " " + p.getUser().getLastName() : null)
                .build();
    }
}
