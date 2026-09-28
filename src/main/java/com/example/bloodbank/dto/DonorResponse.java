package com.example.bloodbank.dto;

import com.example.bloodbank.entity.BloodGroup;
import com.example.bloodbank.entity.Donor;

public class DonorResponse {

    private Long id;
    private String name;
    private String phone;
    private Integer age;
    private BloodGroup bloodGroup;

    public DonorResponse() {
    }

    public DonorResponse(Long id, String name, String phone, Integer age, BloodGroup bloodGroup) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.bloodGroup = bloodGroup;
    }

    public static DonorResponse fromEntity(Donor donor) {
        return new DonorResponse(
            donor.getId(),
            donor.getName(),
            donor.getPhone(),
            donor.getAge(),
            donor.getBloodGroup()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
}
