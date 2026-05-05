package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long userId;
    private String deliveryAddress;
    private String paymentMethod;
    private List<OrderLineRequest> items;
    private String couponCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double deliveryLat;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double deliveryLng;
}
