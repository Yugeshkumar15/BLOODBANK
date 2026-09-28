package com.example.bloodbank.repository;

import com.example.bloodbank.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Optional<Donation> findTopByDonorIdOrderByCollectionDateDesc(Long donorId);

    List<Donation> findByDonorIdOrderByCollectionDateDesc(Long donorId);
}
