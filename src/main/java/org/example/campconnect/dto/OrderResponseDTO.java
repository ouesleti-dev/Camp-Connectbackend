package org.example.campconnect.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long idOrder;
    private Date orderDate;
    private Double totalAmount;
    private String deliveryAddress;
    private String paymentMethod;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private List<OrderLineDTO> orderLines;
    private String orderStatus;
    private String customerPhone;
    private String couponCode;
    private Double discountAmount;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderLineDTO {
        private Long idOrderLine;
        private int requestedQuantity;
        private Double unitPrice;
        private Double totalPrice;
        private String productName;
        private Double productPrice;
    }
}