package org.example.campconnect.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryStatsDTO {
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private long completedDeliveries;
    private long pendingDeliveries;
    private long cancelledDeliveries;
    private long onTheWayDeliveries;
}
