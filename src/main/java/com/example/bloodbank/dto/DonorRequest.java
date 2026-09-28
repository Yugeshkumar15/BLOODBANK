package com.example.bloodbank.dto;

import com.example.bloodbank.entity.BloodGroup;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DonorRequest {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Phone must not be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must contain exactly 10 digits")
    private String phone;

    @NotNull(message = "Age must not be null")
    @Min(value = 18, message = "Age must be between 18 and 65")
    @Max(value = 65, message = "Age must be between 18 and 65")
    private Integer age;

    @NotNull(message = "Blood group must not be null")
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    public DonorRequest() {
    }

    public DonorRequest(String name, String phone, Integer age, BloodGroup bloodGroup) {
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.bloodGroup = bloodGroup;
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
