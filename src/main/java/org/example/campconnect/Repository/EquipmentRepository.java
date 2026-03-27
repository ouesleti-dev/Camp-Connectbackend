package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByOwner(String owner);


    List<Equipment> findByVerifiedTrue();


    List<Equipment> findByVerifiedFalse();
}
