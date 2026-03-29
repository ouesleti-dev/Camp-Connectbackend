package org.example.campconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.campconnect.Entity.Category;
import org.example.campconnect.Entity.ProductState;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Product name is required")
    private String nameProduct;

    @NotBlank(message = "Description is required")
    private String descriptionProduct;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double priceProduct;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantityProduct;

    private String photoProduct; // optional, no validation

    @NotBlank(message = "Location is required")
    private String locationProduct;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Product state is required")
    private ProductState productState;
}
