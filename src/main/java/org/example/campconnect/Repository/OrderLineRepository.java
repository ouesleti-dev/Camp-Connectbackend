package org.example.campconnect.Repository;

import org.example.campconnect.Entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    @Modifying
    @Query(value = "DELETE FROM orders_order_lines WHERE order_lines_id_order_line IN (SELECT id_order_line FROM order_line WHERE product_id_product = :productId)", nativeQuery = true)
    void deleteFromJoinTableByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM OrderLine ol WHERE ol.product.idProduct = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}

