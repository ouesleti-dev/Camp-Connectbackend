package org.example.campconnect.Service;

import org.example.campconnect.dto.CampingCreateRequest;
import org.example.campconnect.dto.CampingDTO;
import org.example.campconnect.dto.CampingUpdateRequest;

import java.util.List;

public interface ICampingService {

    CampingDTO createCamping(CampingCreateRequest request);

    CampingDTO getCampingById(Long id);

    List<CampingDTO> getAllCampings();

    List<CampingDTO> getCampingsByStatus(String status);

    CampingDTO updateCamping(Long id, CampingUpdateRequest request);

    void deleteCamping(Long id);
}
