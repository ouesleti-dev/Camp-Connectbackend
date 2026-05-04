package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Event;
import org.example.campconnect.Repository.CampingRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.Repository.ParticipationRepository;
import org.example.campconnect.dto.CampingRankingDTO;
import org.example.campconnect.dto.EventStatsDTO;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IStatsServiceImp implements IStatsService {

    private final EventRepository eventRepository;
    private final CampingRepository campingRepository;
    private final ParticipationRepository participationRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Fonctionnalité 1 : Stats complètes d'un Event ────────
    @Override
    public EventStatsDTO getEventStats(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Événement introuvable avec l'ID : " + eventId));

        // ⭐ Requêtes JPQL avec JOINs multi-tables
        long totalActivities  = eventRepository.countActivitiesByEvent(eventId);
        long easyActivities   = eventRepository.countActivitiesByEventAndDifficulty(eventId, "EASY");
        long mediumActivities = eventRepository.countActivitiesByEventAndDifficulty(eventId, "MEDIUM");
        long hardActivities   = eventRepository.countActivitiesByEventAndDifficulty(eventId, "HARD");

        // JOIN Activity → Participation
        long totalParticipations = eventRepository.countParticipationsByEvent(eventId);

        // Taux de remplissage
        double fillRate = event.getMaxParticipants() > 0
                ? Math.round((totalParticipations * 100.0 / event.getMaxParticipants()) * 10.0) / 10.0
                : 0.0;

        // JOIN Post
        long totalPosts = eventRepository.countPostsByEvent(eventId);

        // JOIN Post → Comment
        long totalComments = eventRepository.countCommentsByEvent(eventId);

        // JOIN Ticket par statut
        long validTickets     = eventRepository.countTicketsByEventAndStatus(eventId, "VALID");
        long usedTickets      = eventRepository.countTicketsByEventAndStatus(eventId, "USED");
        long cancelledTickets = eventRepository.countTicketsByEventAndStatus(eventId, "CANCELLED");
        long totalTickets     = validTickets + usedTickets + cancelledTickets;

        return EventStatsDTO.builder()
                .eventId(eventId)
                .eventTitle(event.getTitle())
                .eventDate(event.getEventDate() != null
                        ? FORMATTER.format(event.getEventDate()) : null)
                .status(event.getStatus())
                .campingName(event.getCamping() != null
                        ? event.getCamping().getName() : null)
                .totalActivities(totalActivities)
                .easyActivities(easyActivities)
                .mediumActivities(mediumActivities)
                .hardActivities(hardActivities)
                .totalParticipations(totalParticipations)
                .maxParticipants(event.getMaxParticipants())
                .fillRate(fillRate)
                .totalPosts(totalPosts)
                .totalComments(totalComments)
                .totalTickets(totalTickets)
                .validTickets(validTickets)
                .usedTickets(usedTickets)
                .cancelledTickets(cancelledTickets)
                .build();
    }

    @Override
    public List<CampingRankingDTO> getCampingRanking() {

        List<Object[]> rawResults = campingRepository.findCampingRankingRaw();
        List<CampingRankingDTO> rankings = new ArrayList<>();

        for (Object[] row : rawResults) {
            Long   campingId    = (Long)   row[0];
            String name         = (String) row[1];
            String status       = (String) row[2];
            long   totalEvents  = ((Number) row[3]).longValue();
            long   totalActs    = ((Number) row[4]).longValue();
            long   totalParts   = ((Number) row[5]).longValue();
            double totalWaste   = ((Number) row[6]).doubleValue();

            // JOIN Camping → Event → Post
            long totalPosts   = campingRepository.countPostsByCamping(campingId);

            // JOIN Camping → Event → Ticket
            long totalTickets = campingRepository.countTicketsByCamping(campingId);

            // ⭐ Calcul taux remplissage avec données JOIN
            List<Object[]> fillData =
                    campingRepository.getFillRateDataByCamping(campingId);

            double avgFillRate = 0.0;
            if (!fillData.isEmpty()) {
                double totalFill = 0.0;
                int    count     = 0;
                for (Object[] fr : fillData) {
                    int  maxP  = ((Number) fr[0]).intValue();
                    long parts = ((Number) fr[1]).longValue();
                    if (maxP > 0) {
                        totalFill += (parts * 100.0) / maxP;
                        count++;
                    }
                }
                if (count > 0)
                    avgFillRate = Math.round((totalFill / count) * 10.0) / 10.0;
            }

            // Score d'engagement pondéré
            int engagementScore = (int) (
                    totalParts   * 3 +
                            totalTickets * 2 +
                            totalPosts       +
                            totalEvents  * 5
            );

            rankings.add(CampingRankingDTO.builder()
                    .campingId(campingId)
                    .campingName(name)
                    .campingStatus(status)
                    .totalEvents(totalEvents)
                    .totalActivities(totalActs)
                    .totalParticipations(totalParts)
                    .totalPosts(totalPosts)
                    .totalTickets(totalTickets)
                    .totalWasteCollected(totalWaste)
                    .avgFillRate(avgFillRate)
                    .engagementScore(engagementScore)
                    .build());
        }

        rankings.sort((a, b) ->
                Integer.compare(b.getEngagementScore(), a.getEngagementScore()));

        return rankings;
    }
    @Override
    public List<Map<String, Object>> getDropoutFeatures() {

        // ⭐ JPQL avec JOIN multi-tables pour extraire les features ML
        List<Object[]> raw = participationRepository.findDropoutFeaturesRaw();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> feature = new HashMap<>();

            String userEmail   = (String) row[0];
            String status      = (String) row[1];
            String difficulty  = (String) row[2];
            Integer month      = (Integer) row[3];
            Long   totalByUser = ((Number) row[4]).longValue();
            Long   cancelByUser= ((Number) row[5]).longValue();

            // Calculs features
            double cancelRate = totalByUser > 0
                    ? (double) cancelByUser / totalByUser : 0.0;

            int diffScore = switch (difficulty) {
                case "EASY"   -> 1;
                case "MEDIUM" -> 2;
                case "HARD"   -> 3;
                default       -> 1;
            };

            feature.put("cancel_rate_user",      Math.round(cancelRate * 100.0) / 100.0);
            feature.put("activity_difficulty",   diffScore);
            feature.put("nb_participations_user",totalByUser);
            feature.put("month",                 month);
            feature.put("cancelled",             "CANCELLED".equals(status) ? 1 : 0);

            result.add(feature);
        }
        return result;
    }





}