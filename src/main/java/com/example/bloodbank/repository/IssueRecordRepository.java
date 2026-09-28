package com.example.bloodbank.repository;

import com.example.bloodbank.entity.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

    List<IssueRecord> findByBloodUnitId(Long bloodUnitId);
}
