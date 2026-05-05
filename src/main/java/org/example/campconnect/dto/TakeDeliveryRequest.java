package org.example.campconnect.dto;
import lombok.*;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TakeDeliveryRequest {
    private Long userId;
    private Date estimatedDeliveryDate;

}
