package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Product;
import org.example.campconnect.Entity.ProductState;
import org.example.campconnect.Entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product,Long> {
    List<Product> findByProductStatus(ProductStatus status);
    List<Product> findByUserIdUser(Long idUser);
    List<Product> findByProductStatusAndProductState(ProductStatus status, ProductState state);
}
