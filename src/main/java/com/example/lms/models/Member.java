package com.example.lms.models;

import java.time.LocalDate;

public class Member {
    private int memberId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;
    private String status;

    // Full constructor
    public Member(int memberId, String name, String email, String phone, 
                  String address, LocalDate registrationDate, String status) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Constructor for adding new members
    public Member(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = LocalDate.now();
        this.status = "Active";
    }

    // Getters
    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public String getStatus() { return status; }

    // Setters
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
