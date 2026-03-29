package org.example.campconnect.Service;

import org.example.campconnect.dto.ActivityCreateRequest;
import org.example.campconnect.dto.ActivityDTO;
import org.example.campconnect.dto.ActivityUpdateRequest;

import java.util.List;

public interface IActivityService {

    ActivityDTO createActivity(ActivityCreateRequest request);

    ActivityDTO getActivityById(Long id);

    List<ActivityDTO> getAllActivities();

    List<ActivityDTO> getActivitiesByEvent(Long eventId);

    List<ActivityDTO> getActivitiesByCamping(Long campingId);

    List<ActivityDTO> getActivitiesByDifficulty(String difficulty);

    ActivityDTO updateActivity(Long id, ActivityUpdateRequest request);

    void deleteActivity(Long id);
}
