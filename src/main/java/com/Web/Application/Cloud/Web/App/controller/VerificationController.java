package com.Web.Application.Cloud.Web.App.controller;

import com.Web.Application.Cloud.Web.App.entity.User;
import com.Web.Application.Cloud.Web.App.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import java.util.Optional;
import java.util.UUID;

@RestController
public class VerificationController {

    @Autowired
    private UserService userService;

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        // Retrieve the user by token (assuming you have a method for this in UserService)
        UUID id = UUID.fromString(token);
        Optional<User> optionalUser = userService.getUserById(id);

        if (optionalUser == null) {
            return ResponseEntity.badRequest().body("Invalid token or user not found");
        }
        User user = optionalUser.get();

        // Check if the verification link has expired
        LocalDateTime expirationTime = user.getVerification_expiration();
        LocalDateTime currentTime = LocalDateTime.now();
        if (expirationTime == null || currentTime.isAfter(expirationTime)) {
            return ResponseEntity.badRequest().body("Verification link has expired");
        }

        user.setIs_verified(1);
        userService.saveUser(user);
        return ResponseEntity.ok("User verified successfully");
    }
}
