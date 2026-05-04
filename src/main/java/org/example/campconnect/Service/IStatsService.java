package org.example.campconnect.Service;



import org.example.campconnect.dto.CampingRankingDTO;
import org.example.campconnect.dto.EventStatsDTO;

import java.util.List;
import java.util.Map;

public interface IStatsService {

    // Stats complètes d'un event (multi-tables)
    EventStatsDTO getEventStats(Long eventId);

    // Classement des campings les plus actifs (GROUP BY multi-tables)
    List<CampingRankingDTO> getCampingRanking();

    List<Map<String, Object>> getDropoutFeatures();
}