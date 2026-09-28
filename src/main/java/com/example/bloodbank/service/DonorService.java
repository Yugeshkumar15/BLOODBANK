package com.example.bloodbank.service;

import com.example.bloodbank.dto.DonorRequest;
import com.example.bloodbank.dto.DonorResponse;
import com.example.bloodbank.entity.Donor;
import com.example.bloodbank.exception.BusinessException;
import com.example.bloodbank.exception.ResourceNotFoundException;
import com.example.bloodbank.repository.DonorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DonorService {

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    @Transactional
    public DonorResponse createDonor(DonorRequest request) {
        if (donorRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Donor with phone number '" + request.getPhone() + "' already exists.");
        }

        Donor donor = new Donor(
                request.getName(),
                request.getPhone(),
                request.getAge(),
                request.getBloodGroup()
        );

        Donor savedDonor = donorRepository.save(donor);
        return DonorResponse.fromEntity(savedDonor);
    }

    @Transactional(readOnly = true)
    public List<DonorResponse> getAllDonors() {
        return donorRepository.findAll()
                .stream()
                .map(DonorResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Donor getDonorEntityById(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with ID: " + id));
    }
}
