package org.example.campconnect.dto;
import lombok.*;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TakeDeliveryRequest {
    private Long userId;
    private Date estimatedDeliveryDate;

}
