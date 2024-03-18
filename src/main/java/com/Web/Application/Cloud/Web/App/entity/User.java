package com.Web.Application.Cloud.Web.App.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.lang.reflect.Field;
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

    @PrePersist
    protected void onCreate() {
        this.account_created = LocalDateTime.now();
        this.account_updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.account_updated = LocalDateTime.now();
    }





}
