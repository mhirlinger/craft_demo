package com.example.demo.restservice;

public class UserInfo {

    private Long   userId;
    private String firstName;
    private String lastName;
    private String dob;
    private String email;
    private String phoneNumber;

    public UserInfo(Long userId, String firstName, String lastName, String dob, String email, String phoneNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}