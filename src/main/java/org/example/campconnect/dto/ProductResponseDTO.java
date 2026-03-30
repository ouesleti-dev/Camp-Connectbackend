package org.example.campconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.campconnect.Entity.Category;
import org.example.campconnect.Entity.ProductState;
import org.example.campconnect.Entity.ProductStatus;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long idProduct;
    private String nameProduct;
    private String descriptionProduct;
    private Double priceProduct;
    private Integer quantityProduct;
    private String photoProduct;
    private String locationProduct;
    private Category category;
    private ProductState productState;
    private ProductStatus productStatus;
    private Date addedDate;
    private Long userId;
    private String sellerName;
}
