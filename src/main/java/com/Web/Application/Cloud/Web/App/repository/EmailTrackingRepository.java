package com.Web.Application.Cloud.Web.App.repository;

import com.Web.Application.Cloud.Web.App.entity.EmailTracking;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTrackingRepository extends JpaRepository<EmailTracking, UUID> {
    // Custom query methods can be defined here if needed
}
