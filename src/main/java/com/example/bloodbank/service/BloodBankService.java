package com.example.bloodbank.service;

import com.example.bloodbank.dto.BloodUnitResponse;
import com.example.bloodbank.dto.DonationRequest;
import com.example.bloodbank.dto.DonationResponse;
import com.example.bloodbank.dto.EligibilityResponse;
import com.example.bloodbank.dto.IssueRequest;
import com.example.bloodbank.dto.IssueResponse;
import com.example.bloodbank.dto.StockResponse;
import com.example.bloodbank.entity.BloodGroup;
import com.example.bloodbank.entity.BloodUnit;
import com.example.bloodbank.entity.Donation;
import com.example.bloodbank.entity.Donor;
import com.example.bloodbank.entity.IssueRecord;
import com.example.bloodbank.exception.BusinessException;
import com.example.bloodbank.repository.BloodUnitRepository;
import com.example.bloodbank.repository.DonationRepository;
import com.example.bloodbank.repository.IssueRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BloodBankService {

    private final DonorService donorService;
    private final DonationRepository donationRepository;
    private final BloodUnitRepository bloodUnitRepository;
    private final IssueRecordRepository issueRecordRepository;

    @Value("${bloodbank.donation.minimum-gap-days:90}")
    private int minimumGapDays;

    @Value("${bloodbank.blood.default-shelf-life-days:42}")
    private int defaultShelfLifeDays;

    @Value("${bloodbank.blood.expiry-warning-days:7}")
    private int expiryWarningDays;

    public BloodBankService(DonorService donorService,
                            DonationRepository donationRepository,
                            BloodUnitRepository bloodUnitRepository,
                            IssueRecordRepository issueRecordRepository) {
        this.donorService = donorService;
        this.donationRepository = donationRepository;
        this.bloodUnitRepository = bloodUnitRepository;
        this.issueRecordRepository = issueRecordRepository;
    }

    @Transactional
    public DonationResponse recordDonation(DonationRequest request) {
        Donor donor = donorService.getDonorEntityById(request.getDonorId());

        LocalDate collectionDate = request.getCollectionDate() != null ? request.getCollectionDate() : LocalDate.now();

        if (collectionDate.isAfter(LocalDate.now())) {
            throw new BusinessException("Collection date cannot be in the future.");
        }

        if (request.getUnits() == null || request.getUnits() < 1) {
            throw new BusinessException("Units must be at least 1.");
        }

        // Rule 1: Check donor eligibility
        Optional<Donation> lastDonationOpt = donationRepository.findTopByDonorIdOrderByCollectionDateDesc(donor.getId());

        if (lastDonationOpt.isPresent()) {
            Donation lastDonation = lastDonationOpt.get();
            LocalDate lastCollectionDate = lastDonation.getCollectionDate();

            if (collectionDate.isBefore(lastCollectionDate)) {
                throw new BusinessException("New collection date (" + collectionDate + ") cannot be before previous donation date (" + lastCollectionDate + ").");
            }

            long daysBetween = ChronoUnit.DAYS.between(lastCollectionDate, collectionDate);
            if (daysBetween < minimumGapDays) {
                long remainingDays = minimumGapDays - daysBetween;
                throw new BusinessException("Donation rejected. Donor must wait at least " + minimumGapDays + " days between donations. Days remaining: " + remainingDays + ".");
            }
        }

        // Rule 2: Automatic blood expiry calculation
        LocalDate expiryDate = collectionDate.plusDays(defaultShelfLifeDays);

        Donation donation = new Donation(donor, collectionDate, expiryDate, request.getUnits());
        Donation savedDonation = donationRepository.save(donation);

        // Update inventory
        BloodUnit bloodUnit = new BloodUnit(
                savedDonation,
                donor.getBloodGroup(),
                collectionDate,
                expiryDate,
                request.getUnits(),
                request.getUnits()
        );
        bloodUnitRepository.save(bloodUnit);

        return DonationResponse.fromEntity(savedDonation);
    }

    @Transactional(readOnly = true)
    public EligibilityResponse checkDonorEligibility(Long donorId) {
        Donor donor = donorService.getDonorEntityById(donorId);

        Optional<Donation> lastDonationOpt = donationRepository.findTopByDonorIdOrderByCollectionDateDesc(donor.getId());

        if (lastDonationOpt.isEmpty()) {
            return new EligibilityResponse(
                    donorId,
                    true,
                    "Eligible to donate. No previous donation record found.",
                    null,
                    null,
                    minimumGapDays
            );
        }

        Donation lastDonation = lastDonationOpt.get();
        LocalDate lastDonationDate = lastDonation.getCollectionDate();
        long daysSince = ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now());

        if (daysSince >= minimumGapDays) {
            return new EligibilityResponse(
                    donorId,
                    true,
                    "Eligible to donate.",
                    lastDonationDate,
                    daysSince,
                    minimumGapDays
            );
        } else {
            long remainingDays = minimumGapDays - daysSince;
            return new EligibilityResponse(
                    donorId,
                    false,
                    "Donation rejected. Donor must wait at least " + minimumGapDays + " days between donations. Days remaining: " + remainingDays + ".",
                    lastDonationDate,
                    daysSince,
                    minimumGapDays
            );
        }
    }

    @Transactional
    public IssueResponse issueBlood(IssueRequest request) {
        LocalDate issueDate = request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now();

        if (issueDate.isAfter(LocalDate.now())) {
            throw new BusinessException("Issue date cannot be in the future.");
        }

        if (request.getUnits() == null || request.getUnits() < 1) {
            throw new BusinessException("Units must be at least 1.");
        }

        // Rule 4 & 5: Find usable units using FEFO (excluding expired and zero available units)
        List<BloodUnit> usableUnits = bloodUnitRepository.findUsableUnitsFEFO(request.getBloodGroup(), issueDate);

        int totalAvailable = usableUnits.stream().mapToInt(BloodUnit::getAvailableUnits).sum();

        if (totalAvailable < request.getUnits()) {
            throw new BusinessException("Insufficient usable stock for " + request.getBloodGroup() + ". Required: " + request.getUnits() + " units, but only " + totalAvailable + " usable units available. Expired units are excluded.");
        }

        int remainingToIssue = request.getUnits();
        List<Long> issueRecordIds = new ArrayList<>();

        for (BloodUnit unit : usableUnits) {
            if (remainingToIssue <= 0) {
                break;
            }

            int take = Math.min(unit.getAvailableUnits(), remainingToIssue);
            unit.setAvailableUnits(unit.getAvailableUnits() - take);
            bloodUnitRepository.save(unit);

            IssueRecord record = new IssueRecord(
                    unit,
                    request.getPatientName(),
                    request.getBloodGroup(),
                    take,
                    issueDate
            );
            IssueRecord savedRecord = issueRecordRepository.save(record);
            issueRecordIds.add(savedRecord.getId());

            remainingToIssue -= take;
        }

        return new IssueResponse(
                request.getPatientName(),
                request.getBloodGroup(),
                request.getUnits(),
                issueDate,
                "Blood issued successfully.",
                issueRecordIds
        );
    }

    @Transactional(readOnly = true)
    public StockResponse getCurrentStock() {
        LocalDate today = LocalDate.now();
        List<Object[]> rawStock = bloodUnitRepository.countAvailableStockGroupedByBloodGroup(today);

        Map<BloodGroup, Integer> stockMap = new EnumMap<>(BloodGroup.class);
        for (BloodGroup group : BloodGroup.values()) {
            stockMap.put(group, 0);
        }

        int totalAvailable = 0;
        for (Object[] row : rawStock) {
            BloodGroup group = (BloodGroup) row[0];
            Long count = (Long) row[1];
            int availableCount = count != null ? count.intValue() : 0;
            stockMap.put(group, availableCount);
            totalAvailable += availableCount;
        }

        return new StockResponse(stockMap, totalAvailable);
    }

    @Transactional(readOnly = true)
    public List<BloodUnitResponse> getExpiringUnits() {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(expiryWarningDays);

        List<BloodUnit> expiringUnits = bloodUnitRepository.findExpiringUnits(today, warningDate);

        return expiringUnits.stream()
                .map(unit -> BloodUnitResponse.fromEntity(unit, today))
                .toList();
    }
}
