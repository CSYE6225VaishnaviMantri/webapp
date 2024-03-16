package com.Web.Application.Cloud.Web.App.controller;

import com.Web.Application.Cloud.Web.App.entity.User;
import com.Web.Application.Cloud.Web.App.entity.UserResponse;
import com.Web.Application.Cloud.Web.App.repository.UserRepository;
import com.Web.Application.Cloud.Web.App.service.HealthCloudService;
import com.Web.Application.Cloud.Web.App.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import static org.springframework.web.servlet.function.RequestPredicates.contentType;


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
            String Base64Credentials = header.substring("Basic".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] split = DecodedCredentials.split(":", 2);
            System.out.println("User credentials whose account details are Fetched are:" + DecodedCredentials);
           

            String SplitUsername = split[0];
            String SplitPassword = split[1];

            User UserObj = UserRepo.findByUsername(SplitUsername);
            if (UserObj == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            boolean AreValidCredentials = Service.AreValidCredentials(SplitUsername, SplitPassword);

            if (AreValidCredentials) {

                UserResponse UserResponseValues = UserResponse.convertToDTO(UserObj);
               
                return ResponseEntity.ok().body(UserResponseValues);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
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
        catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("Error Message", "User with the provided Email Address already exists.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"Error Message\": \"User with the provided Email Address already exists.\"}");

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"Error Message\": \"Invalid User Creation Operation.\"}");
        }
    }



    @PutMapping("/v1/user/self")
    public ResponseEntity<Object> updatingUser(@RequestBody User newUser, @RequestHeader("Authorization") String header) {
        try {
            String Base64Credentials = header.substring("Basic ".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] splitValues = DecodedCredentials.split(":", 2);

            String username = splitValues[0];
            String password = splitValues[1];
            User user = UserRepo.findByUsername(username);

            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            boolean isValidCredentials = Service.AreValidCredentials(username, password);

            if (!isValidCredentials) {
        
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (newUser.getUsername() != null && !newUser.getUsername().isEmpty() ||
                    newUser.getAccount_updated() != null ||
                    newUser.getAccount_created() != null ||
                    newUser.getId() != null) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Username, account_updated, account_created, and id fields should not be provided in the payload.\"}");
            }


            if (!isValidUpdateRequest(newUser)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Invalid update request.\"}");
            }
            if (newUser.getFirst_name() == null || newUser.getFirst_name().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"First Name Field Cannot be Empty\"}");

            }

            if (newUser.getLast_name() == null || newUser.getLast_name().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Last Name Field Cannot be Empty\"}");
            }

            if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Password Field Cannot be Empty\"}");
            }


            updateUserDetails(user, newUser);


        // Save updated user
        user.setAccount_updated(LocalDateTime.now());
        UserRepo.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    private static boolean IsValidPassword(String password) {
        String regularExpression = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(regularExpression);
    }

    private boolean IsValidEmail(String email) {
        String regularExpression = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidUpdateRequest(User newUser) {
        return newUser.getUsername() == null &&
                newUser.getAccount_updated() == null &&
                newUser.getAccount_created() == null &&
                newUser.getId() == null;
    }

    private void updateUserDetails(User user, User newUser) {
        if (newUser.getFirst_name() != null && !newUser.getFirst_name().isEmpty()) {
            user.setFirst_name(newUser.getFirst_name());
        }
        if (newUser.getLast_name() != null && !newUser.getLast_name().isEmpty()) {
            user.setLast_name(newUser.getLast_name());
        }
        if (newUser.getPassword() != null && !newUser.getPassword().isEmpty() && IsValidPassword(newUser.getPassword())) {
            user.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
        }
    }


}





