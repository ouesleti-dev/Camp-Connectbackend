package org.example.campconnect.Repository;

import org.example.campconnect.Entity.offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OfferRepository extends JpaRepository<offer, Long> {}