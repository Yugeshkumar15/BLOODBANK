package com.example.bloodbank.repository;

import com.example.bloodbank.entity.BloodGroup;
import com.example.bloodbank.entity.BloodUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {

    @Query("SELECT b FROM BloodUnit b WHERE b.bloodGroup = :bloodGroup AND b.availableUnits > 0 AND b.expiryDate >= :currentDate ORDER BY b.expiryDate ASC, b.id ASC")
    List<BloodUnit> findUsableUnitsFEFO(@Param("bloodGroup") BloodGroup bloodGroup, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT b FROM BloodUnit b WHERE b.availableUnits > 0 AND b.expiryDate >= :today AND b.expiryDate <= :warningDate ORDER BY b.expiryDate ASC")
    List<BloodUnit> findExpiringUnits(@Param("today") LocalDate today, @Param("warningDate") LocalDate warningDate);

    @Query("SELECT b.bloodGroup, SUM(b.availableUnits) FROM BloodUnit b WHERE b.availableUnits > 0 AND b.expiryDate >= :currentDate GROUP BY b.bloodGroup")
    List<Object[]> countAvailableStockGroupedByBloodGroup(@Param("currentDate") LocalDate currentDate);
}
