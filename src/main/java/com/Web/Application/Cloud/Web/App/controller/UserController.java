package com.Web.Application.Cloud.Web.App.controller;

import ch.qos.logback.classic.Logger;
import com.Web.Application.Cloud.Web.App.entity.User;
import com.Web.Application.Cloud.Web.App.entity.UserResponse;
import com.Web.Application.Cloud.Web.App.repository.UserRepository;
import com.Web.Application.Cloud.Web.App.service.HealthCloudService;
import com.Web.Application.Cloud.Web.App.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
public class UserController {
    @Autowired
    public UserService Service;

    @Autowired
    private UserRepository UserRepo;

    @Autowired
    private HealthCloudService DatabaseConnection;
    private Logger logger;

    @GetMapping("v1/user/self")
    public ResponseEntity<UserResponse> FetchUserInformation(@RequestHeader("Authorization") String header) {
        try {

            if (!DatabaseConnection.DatabaseConnectivity()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
            }

            String token = null;
            String Base64Credentials = header.substring("Basic ".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] split = DecodedCredentials.split(":", 2);

            String SplitUsername = split[0];
            String SplitPassword = split[1];
            User UserObj = UserRepo.findByUsername(SplitUsername);
            if (UserObj == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            boolean AreValidCredentials = Service.AreValidCredentials(SplitUsername, SplitPassword);

            if (AreValidCredentials) {
                UserResponse UserResponseValues = UserResponse.convertToDTO(UserObj);
                return ResponseEntity.ok().body(UserResponseValues);
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("v1/user")
    public ResponseEntity<Object> CreatingUser(@RequestBody User newUser) {
        try {
            if (!DatabaseConnection.DatabaseConnectivity()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
            }
            if (newUser.getUsername() == null || newUser.getUsername().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email Address field is mandatory for creation of user.");
            }

            if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password field is mandatory for creation of user.");
            }

            if (newUser.getFirst_name() == null || newUser.getFirst_name().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First Name is mandatory for creation of user.");
            }

            if (newUser.getLast_name() == null || newUser.getLast_name().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last Name is mandatory for creation of user.");
            }

            if (!isValidEmail(newUser.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address.");
            }

            if (!isValidPassword(newUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
            }

            Service.CreatingUser(newUser);
            UserResponse CreateUserResponseValues = UserResponse.convertToDTO(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateUserResponseValues);
        } catch (Exception e) {

            if (e instanceof DataIntegrityViolationException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the provided email already exists.");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid User Creation Operation.");
        }
    }


    @PutMapping("/v1/user/self")
    public ResponseEntity<Object> updatingUser(@RequestBody User newUser, @RequestHeader("Authorization") String header) {
        try {
            String token = null;
            String Base64Credentials = header.substring("Basic ".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] splitValues = DecodedCredentials.split(":", 2);

            String username = splitValues[0];
            String password = splitValues[1];
            User user = UserRepo.findByUsername(username);

            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            boolean isValidCredentials = Service.AreValidCredentials(username, password);

            if (isValidCredentials) {
                if (!user.getUsername().equals(newUser.getUsername())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this account.");
                }

                if (newUser.getFirst_name() == null || newUser.getFirst_name().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First Name Field Cannot be Empty");
                }

                if(newUser.getLast_name() == null || newUser.getLast_name().isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last Name Field Cannot be Empty");
                }


                user.setFirst_name(newUser.getFirst_name());
                user.setLast_name(newUser.getLast_name());

                if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
                    if (!isValidPassword(newUser.getPassword())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
                    }
                    user.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
                }


                user.setAccount_updated(LocalDateTime.now());
                UserRepo.save(user);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



    @RequestMapping(value = "/v1/user/self", method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Void> V1SelfInvalidMethod(HttpServletRequest request) {
        if (!DatabaseConnection.DatabaseConnectivity()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @RequestMapping(value = "/v1/user", method = {RequestMethod.GET, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Void> V1UserInvalidMethod(HttpServletRequest request) {
        if (!DatabaseConnection.DatabaseConnectivity()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    private boolean isValidPassword(String password) {

        String regularExpression = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(regularExpression);
    }

    private boolean isValidEmail(String email) {

        String regularExpression = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    }





