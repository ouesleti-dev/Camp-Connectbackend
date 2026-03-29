package org.example.campconnect.mapper;

import org.example.campconnect.Entity.Camping;
import org.example.campconnect.dto.CampingCreateRequest;
import org.example.campconnect.dto.CampingDTO;
import org.springframework.stereotype.Component;

@Component
public class CampingMapper {

    public CampingDTO toDto(Camping camping) {
        // ⭐ Ne jamais appeler .size() sur une collection Lazy ici
        // On passe 0 par défaut, le service calculera si besoin
        return CampingDTO.builder()
                .campingId(camping.getCampingId())
                .name(camping.getName())
                .address(camping.getAddress())
                .description(camping.getDescription())
                .postalCode(camping.getPostalCode())
                .status(camping.getStatus())
                .totalEvents(0)      // ← on ne touche plus aux collections ici
                .totalActivities(0)  // ← même chose
                .build();
    }

    public Camping toEntity(CampingCreateRequest req) {
        Camping camping = new Camping();
        camping.setName(req.getName());
        camping.setAddress(req.getAddress());
        camping.setDescription(req.getDescription());
        camping.setPostalCode(req.getPostalCode());
        camping.setStatus(req.getStatus());
        return camping;
    }
}