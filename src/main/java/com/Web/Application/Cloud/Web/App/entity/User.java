package com.Web.Application.Cloud.Web.App.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.*;


import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonProperty("username")
    @Column(nullable = false,name="User_Name")
    @Email
    private  String username;

    //@JsonIgnore
    @JsonProperty("password")
    @Column(nullable = false,name="Password")
    private String password;

    @JsonProperty("first_name")
    @Column(nullable = false,name="FirstName")
    private String first_name;

    @JsonProperty("last_name")
    @Column(nullable = false,name="LastName")
    private String last_name;

    @JsonProperty("accountCreated")
    @Column(updatable = false,name = "Time_Account_Created")
    private LocalDateTime account_created;

    @JsonProperty("accountUpdated")
    @Column(name = "Time_Account_Updated")
    private LocalDateTime account_updated;

    public int getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(int is_verified) {
        this.is_verified = is_verified;
    }



    @JsonProperty("is_verified")
    @Column(nullable = false, name = "Is_Verified")
    private int is_verified; // Use Boolean type


    @Column(name = "verification_expiration")
    private LocalDateTime verification_expiration;


    @PrePersist
    protected void onCreate() {
        this.account_created = LocalDateTime.now();
        this.account_updated = LocalDateTime.now();
        this.is_verified=0;
        this.verification_expiration = LocalDateTime.now().plusMinutes(2);


    }

    private String generateVerificationLink() {
        // Logic to generate verification link (e.g., UUID.randomUUID().toString())
        String verificationLink = "http://vaishnavimantri.me:8080/verify-email?token=" + id ;
        System.out.println("Verification Link: " + verificationLink);
        return verificationLink;
    }

    @PreUpdate
    protected void onUpdate() {
        this.account_updated = LocalDateTime.now();
    }



}
