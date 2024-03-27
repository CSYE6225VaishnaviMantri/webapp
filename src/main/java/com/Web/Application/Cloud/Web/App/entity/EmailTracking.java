package com.Web.Application.Cloud.Web.App.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailTracking {
    @JsonProperty("username")
    @Column(nullable = false, name = "User_Name", unique = true)
    @Email
    private  String username;

    @Id
    @Column(columnDefinition = "VARCHAR(255)")
    private UUID id;


    @Column(name = "link_expiration_time")
    private LocalDateTime link_expiration_time;

    @Column(name = "link_send_time")
    private LocalDateTime link_send_time;
}
