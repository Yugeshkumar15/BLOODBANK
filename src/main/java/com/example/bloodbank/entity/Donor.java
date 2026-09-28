package com.example.bloodbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "donors")
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Phone must not be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must contain exactly 10 digits")
    @Column(nullable = false, unique = true)
    private String phone;

    @NotNull(message = "Age must not be null")
    @Min(value = 18, message = "Age must be between 18 and 65")
    @Max(value = 65, message = "Age must be between 18 and 65")
    @Column(nullable = false)
    private Integer age;

    @NotNull(message = "Blood group must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false)
    private BloodGroup bloodGroup;

    public Donor() {
    }

    public Donor(String name, String phone, Integer age, BloodGroup bloodGroup) {
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.bloodGroup = bloodGroup;
    }

    public Donor(Long id, String name, String phone, Integer age, BloodGroup bloodGroup) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.bloodGroup = bloodGroup;
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
