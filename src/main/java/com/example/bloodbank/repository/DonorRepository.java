package com.example.bloodbank.repository;

import com.example.bloodbank.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByPhone(String phone);

    boolean existsByPhone(String phone);
}
