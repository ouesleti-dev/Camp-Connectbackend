package org.example.campconnect.dto;

import lombok.Data;
import java.util.Date;

@Data
public class NotificationDto {
    private Long    id;
    private String  message;
    private String  type;
    private Boolean isRead;
    private Date    createdAt;
    private String  equipmentName;
    private Date    maintenanceStart;
    private Date    maintenanceEnd;
}