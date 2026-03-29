package org.example.campconnect.mapper;

import org.example.campconnect.Entity.Activity;
import org.example.campconnect.dto.ActivityCreateRequest;
import org.example.campconnect.dto.ActivityDTO;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    // Mapping MANUEL Entity → DTO
    public ActivityDTO toDto(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .description(activity.getDescription())
                .duration(activity.getDuration())
                .difficulty(activity.getDifficulty())
                .eventId(activity.getEvent() != null ? activity.getEvent().getId() : null)
                .eventTitle(activity.getEvent() != null ? activity.getEvent().getTitle() : null)
                .campingId(activity.getCamping() != null ? activity.getCamping().getCampingId() : null)
                .campingName(activity.getCamping() != null ? activity.getCamping().getName() : null)
                .totalParticipations(0)
                .build();
    }

    // Mapping MANUEL DTO → Entity (pour création)
    public Activity toEntity(ActivityCreateRequest req) {
        Activity activity = new Activity();
        activity.setName(req.getName());
        activity.setDescription(req.getDescription());
        activity.setDuration(req.getDuration());
        activity.setDifficulty(req.getDifficulty());
        return activity;
    }
}