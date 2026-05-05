package org.example.campconnect.Service;


import org.example.campconnect.dto.*;
import java.util.Date;
import java.util.List;

public interface IMaintenanceService {
    List<MaintenanceSlotDto>     suggestSlots(
            Long equipmentId, int durationDays, String ownerEmail);
    MaintenanceImpactDto         previewImpact(
            Long equipmentId, Date start, Date end, String ownerEmail);
    MaintenanceResponseDto       confirmMaintenance(
            MaintenanceConfirmDto dto, String ownerEmail);
    List<MaintenanceResponseDto> getHistory(Long equipmentId);
}