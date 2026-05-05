package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class CouponRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotNull @DecimalMin("1") @DecimalMax("100")
    private Double discountPercentage;

    @NotNull
    private Date expirationDate;

    @NotNull @Min(1)
    private Integer maxUses;
}
