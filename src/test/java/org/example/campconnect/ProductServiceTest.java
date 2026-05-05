package org.example.campconnect;

import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.example.campconnect.Service.ProductService;
import org.example.campconnect.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ✅ ligne ajoutée
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProductService productService;

    private User mockUser;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .idUser(1L)
                .firstName("Nadim")
                .lastName("Zarrouk")
                .email("nadim@test.com")
                .build();

        mockProduct = Product.builder()
                .idProduct(1L)
                .nameProduct("Tent")
                .priceProduct(50.0)
                .quantityProduct(5)
                .productStatus(ProductStatus.PENDING)
                .user(mockUser)
                .build();

        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("nadim@test.com");
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    @DisplayName("Should add product with PENDING status")
    void testAddProduct() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setNameProduct("Tent");
        dto.setPriceProduct(50.0);
        dto.setQuantityProduct(5);
        dto.setCategory(Category.TENTS);
        dto.setProductState(ProductState.AVAILABLE);

        when(userRepository.findByEmail("nadim@test.com"))
                .thenReturn(Optional.of(mockUser));
        when(productRepository.save(any(Product.class)))
                .thenReturn(mockProduct);

        ProductResponseDTO result = productService.addProduct(dto);

        assertNotNull(result);
        assertEquals("Tent", result.getNameProduct());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return approved products only")
    void testGetApprovedProducts() {
        mockProduct.setProductStatus(ProductStatus.APPROVED);
        when(productRepository.findByProductStatus(ProductStatus.APPROVED))
                .thenReturn(List.of(mockProduct));

        List<ProductResponseDTO> result = productService.getApprovedProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should approve product")
    void testApproveProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(mockProduct);

        ProductResponseDTO result = productService.approveProduct(1L);

        assertEquals(ProductStatus.APPROVED, mockProduct.getProductStatus());
        verify(productRepository).save(mockProduct);
    }

    @Test
    @DisplayName("Should reject product")
    void testRejectProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(mockProduct);

        productService.rejectProduct(1L);

        assertEquals(ProductStatus.REJECTED, mockProduct.getProductStatus());
    }

    @Test
    @DisplayName("Should delete product")
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw when product not found")
    void testGetProductById_NotFound() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.getProductById(99L));
    }
}