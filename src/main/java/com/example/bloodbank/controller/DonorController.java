package com.example.bloodbank.controller;

import com.example.bloodbank.dto.DonorRequest;
import com.example.bloodbank.dto.DonorResponse;
import com.example.bloodbank.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @PostMapping
    public ResponseEntity<DonorResponse> createDonor(@Valid @RequestBody DonorRequest donorRequest) {
        DonorResponse createdDonor = donorService.createDonor(donorRequest);
        return new ResponseEntity<>(createdDonor, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DonorResponse>> getAllDonors() {
        List<DonorResponse> donors = donorService.getAllDonors();
        return ResponseEntity.ok(donors);
    }
}
