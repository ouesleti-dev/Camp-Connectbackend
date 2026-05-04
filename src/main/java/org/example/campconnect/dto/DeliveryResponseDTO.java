package org.example.campconnect.dto;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponseDTO {
    private Long idDelivery;
    private String departureAddress;
    private String arrivalAddress;
    private Double deliveryFee;
    private String deliveryState;
    private Date estimatedDeliveryDate;

    private Long orderId;
    private String orderDeliveryAddress;
    private Double orderTotalAmount;
    private Date orderDate;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerPhone;

    private Long deliveryPersonId;
    private String deliveryPersonFirstName;
    private String deliveryPersonLastName;
    private String deliveryPersonPhone;
    private Double departureLat;
    private Double departureLng;
    private Double arrivalLat;
    private Double arrivalLng;
}
