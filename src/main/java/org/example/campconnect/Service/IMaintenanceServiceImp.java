package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.example.campconnect.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IMaintenanceServiceImp implements IMaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final RentalRepository      rentalRepository;
    private final EquipmentRepository   equipmentRepository;
    private final UserRepository        userRepository;
    private final NotificationService   notificationService;

    // ── 1. SUGGÉRER DES CRÉNEAUX ──────────────────────────────────
    @Override
    public List<MaintenanceSlotDto> suggestSlots(
            Long equipmentId, int durationDays, String ownerEmail) {

        Equipment eq = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!eq.getOwner().equalsIgnoreCase(ownerEmail))
            throw new RuntimeException("Not authorized");

        List<Rental> rentals =
                rentalRepository.findAcceptedRentalsByEquipment(equipmentId);

        Date today   = stripTime(new Date());
        List<MaintenanceSlotDto> slots = new ArrayList<>();

        // Gap avant premier rental
        if (!rentals.isEmpty()) {
            Date first = stripTime(rentals.get(0).getStartdate());
            if (first.after(today) &&
                    daysBetween(today, first) >= durationDays) {
                Date end = addDays(today, durationDays);
                slots.add(new MaintenanceSlotDto(
                        today, end, daysBetween(end, first)));
            }
        }

        // Gaps entre rentals
        for (int i = 0; i < rentals.size()-1 && slots.size()<5; i++) {
            Date gapStart = stripTime(
                    addDays(rentals.get(i).getEnddate(), 1));
            Date gapEnd = stripTime(
                    rentals.get(i+1).getStartdate());

            if (!gapStart.after(today)) gapStart = today;
            if (gapEnd.before(today))   continue;

            long gap = daysBetween(gapStart, gapEnd);
            if (gap < durationDays) continue;

            Date s = gapStart;
            Date e = addDays(s, durationDays);
            slots.add(new MaintenanceSlotDto(
                    s, e, daysBetween(e, gapEnd)));
        }

        // Gap après dernier rental
        if (slots.size() < 2) {
            Date after = rentals.isEmpty()
                    ? addDays(today, 1)
                    : stripTime(addDays(
                    rentals.get(rentals.size()-1).getEnddate(), 1));
            slots.add(new MaintenanceSlotDto(
                    after, addDays(after, durationDays), 999L));
        }

        // Fallback
        if (slots.isEmpty()) {
            Date s = addDays(today, 1);
            slots.add(new MaintenanceSlotDto(
                    s, addDays(s, durationDays), 999L));
        }

        return slots.stream().limit(5).collect(Collectors.toList());
    }

    // ── 2. PREVIEW IMPACT ─────────────────────────────────────────
    @Override
    public MaintenanceImpactDto previewImpact(
            Long equipmentId, Date start, Date end, String ownerEmail) {

        Equipment eq = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!eq.getOwner().equalsIgnoreCase(ownerEmail))
            throw new RuntimeException("Not authorized");

        List<Rental> affected =
                rentalRepository.findRentalsOverlappingPeriod(
                        equipmentId, start, end);

        List<String> emails = affected.stream()
                .map(Rental::getRenterEmail).distinct()
                .collect(Collectors.toList());

        return new MaintenanceImpactDto(
                affected.size(), emails, start, end, eq.getName());
    }

    // ── 3. CONFIRMER MAINTENANCE ──────────────────────────────────
    @Override
    @Transactional
    public MaintenanceResponseDto confirmMaintenance(
            MaintenanceConfirmDto dto, String ownerEmail) {

        Equipment eq = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!eq.getOwner().equalsIgnoreCase(ownerEmail))
            throw new RuntimeException("Not authorized");

        if (maintenanceRepository.existsOverlappingMaintenance(
                dto.getEquipmentId(),
                dto.getStartDate(), dto.getEndDate()))
            throw new RuntimeException(
                    "Maintenance déjà planifiée sur cette période");

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Bloquer l'équipement
        eq.setState(State.Maintenance);
        equipmentRepository.save(eq);

        // Sauvegarder
        Maintenance saved = maintenanceRepository.save(
                Maintenance.builder()
                        .kind(Kind.valueOf(dto.getKind()))
                        .description(dto.getDescription())
                        .startdate(dto.getStartDate())
                        .enddate(dto.getEndDate())
                        .equipment(eq)
                        .user(owner)
                        .build()
        );

        // Renters affectés
        List<String> affectedEmails = rentalRepository
                .findRentalsOverlappingPeriod(
                        dto.getEquipmentId(),
                        dto.getStartDate(), dto.getEndDate())
                .stream().map(Rental::getRenterEmail).distinct()
                .collect(Collectors.toList());

        // Tous les users
        List<String> allEmails = userRepository.findAll()
                .stream().map(User::getEmail)
                .collect(Collectors.toList());

        // Envoyer notifications
        if (!affectedEmails.isEmpty())
            notificationService.notifyAffectedRenters(
                    affectedEmails, eq.getName(),
                    eq.getIdEquipement(),
                    dto.getStartDate(), dto.getEndDate());

        notificationService.notifyAllUsers(
                allEmails, affectedEmails,
                eq.getName(), eq.getIdEquipement(),
                dto.getStartDate(), dto.getEndDate());

        return toDto(saved);
    }

    // ── 4. HISTORIQUE ─────────────────────────────────────────────
    @Override
    public List<MaintenanceResponseDto> getHistory(Long equipmentId) {
        return maintenanceRepository
                .findByEquipment_IdEquipement(equipmentId)
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    // ── HELPERS ───────────────────────────────────────────────────
    private Date addDays(Date d, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(d); c.add(Calendar.DATE, days);
        return c.getTime();
    }
    private long daysBetween(Date a, Date b) {
        return TimeUnit.MILLISECONDS.toDays(
                b.getTime() - a.getTime());
    }
    private Date stripTime(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    private MaintenanceResponseDto toDto(Maintenance m) {
        MaintenanceResponseDto dto = new MaintenanceResponseDto();
        dto.setId(m.getIdmaintenance());
        dto.setStartDate(m.getStartdate());
        dto.setEndDate(m.getEnddate());
        dto.setDescription(m.getDescription());
        dto.setKind(m.getKind() != null ? m.getKind().name() : null);
        dto.setEquipmentId(m.getEquipment().getIdEquipement());
        dto.setEquipmentName(m.getEquipment().getName());
        return dto;
    }
}