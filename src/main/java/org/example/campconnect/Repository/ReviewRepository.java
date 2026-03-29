package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByEquipment_IdEquipement(Long equipmentId);
    Optional<Review> findByUser_EmailAndEquipment_IdEquipement(String email, Long equipmentId);
    void deleteByEquipment_IdEquipement(Long equipmentId);
}