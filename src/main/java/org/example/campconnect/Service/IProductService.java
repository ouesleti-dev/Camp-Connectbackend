package org.example.campconnect.Service;

import org.example.campconnect.dto.ProductRequestDTO;
import org.example.campconnect.dto.ProductResponseDTO;

import java.util.List;

public interface IProductService {
    ProductResponseDTO addProduct(ProductRequestDTO dto);
    List<ProductResponseDTO> getApprovedProducts();
    List<ProductResponseDTO> getAllProducts();
    List<ProductResponseDTO> getPendingProducts();
    List<ProductResponseDTO> getMyProducts();
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);
    void deleteProduct(Long id);
    ProductResponseDTO approveProduct(Long id);
    ProductResponseDTO rejectProduct(Long id);
}
