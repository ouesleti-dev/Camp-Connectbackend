package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campconnect.dto.FeePreviewDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class DeliveryFeeService {

    private static final double BASE_FEE = 2.0;
    private static final double FEE_PER_KM = 0.15;
    private static final double MIN_FEE = 3.0;
    private static final double MAX_FEE = 35.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    // GeocodingService gardé en fallback seulement
    private final GeocodingService geocodingService;

    // ── Calcul direct avec coordonnées (zéro appel Nominatim) ─
    public FeePreviewDTO calculateFeeFromCoords(
            double fromLat, double fromLng,
            double toLat,   double toLng,
            String departureAddress, String arrivalAddress) {

        double distanceKm = round2(haversine(fromLat, fromLng, toLat, toLng));
        double raw  = BASE_FEE + (distanceKm * FEE_PER_KM);
        double fee  = round2(Math.min(MAX_FEE, Math.max(MIN_FEE, raw)));

        String breakdown = BASE_FEE + " + (" + distanceKm + "km × "
                + FEE_PER_KM + ") = " + fee + " TND";

        log.info("Fee calculated from coords: {}km → {} TND", distanceKm, fee);

        return FeePreviewDTO.builder()
                .distanceKm(distanceKm)
                .baseFee(BASE_FEE)
                .feePerKm(FEE_PER_KM)
                .calculatedFee(fee)
                .breakdown(breakdown)
                .build();
    }

    // ── Fallback : géocode si pas de coords (évite 429 avec cache) ─
    public FeePreviewDTO calculateFeePreview(
            String departureAddress, String arrivalAddress) {

        double[] from = geocodingService.geocode(departureAddress);
        if (from != null) {
            try { Thread.sleep(1100); } catch (InterruptedException ignored) {}
        }
        double[] to = geocodingService.geocode(arrivalAddress);

        if (from == null || to == null) {
            log.warn("Geocoding failed — applying base fee");
            return buildFallback();
        }

        return calculateFeeFromCoords(
                from[0], from[1], to[0], to[1],
                departureAddress, arrivalAddress
        );
    }

    public double calculateFee(String dep, String arr) {
        return calculateFeePreview(dep, arr).getCalculatedFee();
    }

    private double haversine(double lat1, double lon1,
                             double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    private FeePreviewDTO buildFallback() {
        return FeePreviewDTO.builder()
                .distanceKm(0.0).baseFee(BASE_FEE).feePerKm(FEE_PER_KM)
                .calculatedFee(BASE_FEE)
                .breakdown("Geocoding unavailable — base fee: " + BASE_FEE + " TND")
                .build();
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
