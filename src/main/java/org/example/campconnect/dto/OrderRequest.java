package org.example.campconnect.dto;

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
}
