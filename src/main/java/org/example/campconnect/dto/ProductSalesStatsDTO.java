package org.example.campconnect.dto;
import lombok.*;
import org.example.campconnect.Entity.Category;
import org.example.campconnect.Entity.ProductState;
import org.example.campconnect.Entity.ProductStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSalesStatsDTO {
    private Long idProduct;
    private String nameProduct;
    private String category;
    private Double priceProduct;
    private String productState;
    private String productStatus;
    private Long totalQuantitySold;
    private Double totalRevenue;
    private Long distinctBuyers;
}