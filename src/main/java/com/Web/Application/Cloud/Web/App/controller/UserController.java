package com.Web.Application.Cloud.Web.App.controller;

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
import org.springframework.http.MediaType;
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


    @GetMapping("/v1/user/self")
    public ResponseEntity<UserResponse> FetchUserInformation(@RequestHeader("Authorization") String header) {
        try {
            if (!DatabaseConnection.DatabaseConnectivity()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
            }

            String token = null;
            String Base64Credentials = header.substring("Basic".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] split = DecodedCredentials.split(":", 2);
            System.out.println("credentials" + DecodedCredentials);

            String SplitUsername = split[0];
            String SplitPassword = split[1];

            User UserObj = UserRepo.findByUsername(SplitUsername);
            if (UserObj == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            boolean AreValidCredentials = Service.AreValidCredentials(SplitUsername,SplitPassword);

            if (AreValidCredentials) {

                UserResponse UserResponseValues = UserResponse.convertToDTO(UserObj);

                return ResponseEntity.ok().body(UserResponseValues);
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/v1/user")
    public ResponseEntity<Object> CreatingUser(@RequestBody User NewUser) {
        try {

            if (!DatabaseConnection.DatabaseConnectivity()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
            }

            if (NewUser.getUsername() == null || NewUser.getUsername().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Email Address field is mandatory for creation of user.\"}");
            }

            if (NewUser.getPassword() == null || NewUser.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Password field is mandatory for creation of user.\"}");
            }

            if (NewUser.getFirst_name() == null || NewUser.getFirst_name().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"First Name is mandatory for creation of user.\"}");
            }
            if (NewUser.getLast_name() == null || NewUser.getLast_name().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Last Name is mandatory for creation of user.\"}");
            }

            if (!IsValidEmail(NewUser.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Invalid Email Address.\"}");
            }

            if (NewUser.getPassword() != null && !IsValidPassword(NewUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(
                        "{\"Error Message:\": \"Invalid password. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.\"}");
            }

            Service.CreatingUser(NewUser);
            UserResponse CreateuserResponse = UserResponse.convertToDTO(NewUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CreateuserResponse);
        }
        catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body("User with the provided Email Address already exists.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body("Invalid User Creation Operation.");
        }
    }


    @PutMapping("/v1/user/self")
    public ResponseEntity<Object> UpdatingUser(@RequestBody Map<String, String> requestBody, @RequestHeader("Authorization") String header) {
        try {
            String Base64Credentials = header.substring("Basic".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] split = DecodedCredentials.split(":", 2);
            System.out.println("credentials" + DecodedCredentials);

            String SplitUsername = split[0];
            String SplitPassword = split[1];

            User UpdateUserObj = UserRepo.findByUsername(SplitUsername);
            if (UpdateUserObj == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            boolean ValidCredentials = Service.AreValidCredentials(SplitUsername,SplitPassword);

            if (ValidCredentials) {

                Set<String> AllowedFields = new HashSet<>(Arrays.asList("first_name", "last_name", "password"));
                Set<String> RequestBodyFields = requestBody.keySet();
                for (String Field : RequestBodyFields) {
                    if (!AllowedFields.contains(Field)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("{\"Error Message:\": \"Field '" + Field + "' is not allowed\"}");
                    }
                }

                if (!UpdateUserObj.getUsername().equals(SplitUsername)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"Error Message:\": \"Not Authorized to update username\"}");
                }

                String FName = requestBody.get("first_name");
                String LName = requestBody.get("last_name");
                String NewPassword = requestBody.get("password");

                if (FName == null || FName.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"Error Message:\": \"First Name is mandatory\"}");
                }

                if (LName == null || LName.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"Error Message:\": \"Last Name is mandatory\"}");
                }

                if (NewPassword == null || NewPassword.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"Error Message:\": \"Password is mandatory\"}");
                }

                if (!IsValidPassword(NewPassword)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"Error Message:\": \"Invalid password. Please enter password containing atleast one uppercase, one lowercase, and one digit and minimum length of 8\"}");
                }

                UpdateUserObj.setFirst_name(FName);
                UpdateUserObj.setLast_name(LName);
                UpdateUserObj.setPassword(new BCryptPasswordEncoder().encode(NewPassword));
                UpdateUserObj.setAccount_updated(LocalDateTime.now());

                UserRepo.save(UpdateUserObj);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            else {
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

    private boolean IsValidPassword(String password) {

        String regularExpression = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(regularExpression);
    }

    private boolean IsValidEmail(String email) {

        String regularExpression = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    }

