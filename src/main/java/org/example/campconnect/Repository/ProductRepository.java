package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Product;
import org.example.campconnect.Entity.ProductState;
import org.example.campconnect.Entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product,Long> {
    List<Product> findByProductStatus(ProductStatus status);
    List<Product> findByUserIdUser(Long idUser);
    List<Product> findByProductStatusAndProductState(ProductStatus status, ProductState state);
    @Query("""
    SELECT p.idProduct,
           p.nameProduct,
           p.category,
           p.priceProduct,
           p.productState,
           p.productStatus,
           SUM(ol.requestedQuantity),
           SUM(ol.totalPrice),
           COUNT(DISTINCT o.user.idUser)
    FROM Product p
    JOIN p.orderLines ol
    JOIN ol.product op
    JOIN Order o ON ol MEMBER OF o.orderLines
    WHERE p.productStatus = :status
    GROUP BY p.idProduct, p.nameProduct, p.category,
             p.priceProduct, p.productState, p.productStatus
    ORDER BY SUM(ol.totalPrice) DESC
""")
    List<Object[]> findProductSalesStats(@Param("status") ProductStatus status);
}
