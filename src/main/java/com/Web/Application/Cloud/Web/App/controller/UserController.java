package com.Web.Application.Cloud.Web.App.controller;

import com.Web.Application.Cloud.Web.App.entity.User;
import com.Web.Application.Cloud.Web.App.entity.UserResponse;
import com.Web.Application.Cloud.Web.App.repository.UserRepository;
import com.Web.Application.Cloud.Web.App.service.HealthCloudService;
import com.Web.Application.Cloud.Web.App.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
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

    private static final Logger log = LogManager.getLogger(UserController.class);

    @GetMapping("/v1/user/self")
    public ResponseEntity<UserResponse> FetchUserInformation(@RequestHeader("Authorization") String header, HttpServletRequest request, HttpServletResponse response) {


        ThreadContext.put("severity", "INFO");
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("path", request.getRequestURI());
        ThreadContext.put("RequestBody",header);
        ThreadContext.put("responseBody","No Response Body returned here");
        log.info("Fetching the User Details.");

        try {
            if (!DatabaseConnection.DatabaseConnectivity()) {

                ThreadContext.put("severity", "ERROR");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",header);
                ThreadContext.put("responseBody","No Response Body returned here");
                log.error("Database connectivity issue. Service unavailable.");

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
            }

            String Base64Credentials = header.substring("Basic".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] split = DecodedCredentials.split(":", 2);


            String SplitUsername = split[0];
            String SplitPassword = split[1];

            User UserObj = UserRepo.findByUsername(SplitUsername);
            if (UserObj == null) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",header);
                ThreadContext.put("responseBody","No Response Body returned here");
                log.warn("Unauthorized access:Because Username is not found here.");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            boolean AreValidCredentials = Service.AreValidCredentials(SplitUsername, SplitPassword);

            if (AreValidCredentials) {
                UserResponse UserResponseValues = UserResponse.convertToDTO(UserObj);

                ThreadContext.put("severity", "INFO");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",header);
                ThreadContext.put("responseBody", UserObj.toString());
                log.info("User information fetched successfully.");

                return ResponseEntity.ok().body(UserResponseValues);
            }

            else {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",header);
                ThreadContext.put("responseBody","No Response Body returned here");
                log.warn("Unauthorized access: Invalid credentials.");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        }
        catch (Exception e) {
            ThreadContext.put("severity", "ERROR");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody",header);
            ThreadContext.put("responseBody","No Response Body returned here");
            log.error("Error fetching user information: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        finally {
            ThreadContext.clearAll();
        }
    }

    @PostMapping("/v1/user")
    public ResponseEntity<Object> CreatingUser(@RequestBody User NewUser,HttpServletRequest request) {
      

        try {

            ThreadContext.put("severity", "INFO");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody",NewUser.toString());
            ThreadContext.put("responseBody","No Response Body returned here");
            log.info("Creating the User.");

            if (!DatabaseConnection.DatabaseConnectivity()) {

                ThreadContext.put("severity", "ERROR");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",NewUser.toString());
                ThreadContext.put("responseBody","No Response Body returned here");
                log.error("Database connectivity issue. Service unavailable.");

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
            }

            if (NewUser.getUsername() == null || NewUser.getUsername().isEmpty()) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",NewUser.toString());
                ThreadContext.put("responseBody","{\"Error Message:\": \"Email Address field is mandatory for creation of user.\"}");
                log.warn("Email Address field is mandatory for creation of user.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Email Address field is mandatory for creation of user.\"}");
            }

            if (NewUser.getPassword() == null || NewUser.getPassword().isEmpty()) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",NewUser.toString());
                ThreadContext.put("responseBody","{\"Error Message:\": \"Password field is mandatory for creation of user.\"}");
                log.warn("Password field is mandatory for creation of user.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Password field is mandatory for creation of user.\"}");
            }

            if (NewUser.getFirst_name() == null || NewUser.getFirst_name().isEmpty()) {
                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",NewUser.toString());
                ThreadContext.put("responseBody","{\"Error Message:\": \"First Name field is mandatory for creation of user.\"}");
                log.warn("First Name field is mandatory for creation of user.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"First Name field is mandatory for creation of user.\"}");
            }

            if (NewUser.getLast_name() == null || NewUser.getLast_name().isEmpty()) {
                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",NewUser.toString());
                ThreadContext.put("responseBody","{\"Error Message:\": \"Last Name field is mandatory for creation of user.\"}");
                log.warn("Last Name field is mandatory for creation of user.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Last Name field is mandatory for creation of user.\"}");
            }

            if (!IsValidEmail(NewUser.getUsername())) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",NewUser.toString());
                ThreadContext.put("responseBody","{\"Error Message:\": \"Invalid Email Address for creation of user.\"}");
                log.warn("Invalid Email Address for creation of user.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message:\": \"Invalid Email Address for creation of user.\"}");
            }

            if (NewUser.getPassword() != null && !IsValidPassword(NewUser.getPassword())) {
                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",NewUser.toString());
                ThreadContext.put("responseBody","{\"Error Message:\": \"Invalid password. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.\"}");
                log.warn("Invalid Password Field for creation of user.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(
                        "{\"Error Message:\": \"Invalid password. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.\"}");
            }

            Service.CreatingUser(NewUser);
            UserResponse CreateuserResponse = UserResponse.convertToDTO(NewUser);

            ThreadContext.put("severity", "INFO");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody",NewUser.toString());
            ThreadContext.put("responseBody",CreateuserResponse.toString());

            log.info("User created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CreateuserResponse);
        }
        catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("Error Message", "User with the provided Email Address already exists.");

            ThreadContext.put("severity", "ERROR");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody",NewUser.toString());
            ThreadContext.put("responseBody","{\"Error Message\": \"User with the provided Email Address already exists.\"}");

            log.error("User with the provided Email Address already exists.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"Error Message\": \"User with the provided Email Address already exists.\"}");

        }
        
        catch (Exception e) {
            ThreadContext.put("severity", "ERROR");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody",NewUser.toString());
            ThreadContext.put("responseBody","{\"Error Message\": \"Invalid User Creation Operation.\"}");

            log.error("Invalid User Creation Operation: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"Error Message\": \"Invalid User Creation Operation.\"}");
        }
        finally {
            ThreadContext.clearAll();
        }

    }




    @PutMapping("/v1/user/self")
    public ResponseEntity<Object> updatingUser(@RequestBody User newUser, @RequestHeader("Authorization") String header,HttpServletRequest request) {

        ThreadContext.put("severity", "INFO");
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("path", request.getRequestURI());
        ThreadContext.put("RequestBody",header);
        ThreadContext.put("responseBody","No Response Body returned here");
        log.info("Updating user...");

        try {
            String Base64Credentials = header.substring("Basic ".length()).trim();
            String DecodedCredentials = new String(Base64.getDecoder().decode(Base64Credentials), StandardCharsets.UTF_8);
            String[] splitValues = DecodedCredentials.split(":", 2);

            String username = splitValues[0];
            String password = splitValues[1];
            User user = UserRepo.findByUsername(username);

            if (user == null) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",header);
                ThreadContext.put("responseBody","No Response Body returned here");
                log.warn("User not found for update.");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            boolean isValidCredentials = Service.AreValidCredentials(username, password);

            if (!isValidCredentials) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",header);
                ThreadContext.put("responseBody","No Response Body returned here");
                log.warn("Invalid credentials for user update.");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (newUser.getUsername() != null && !newUser.getUsername().isEmpty() ||
                    newUser.getAccount_updated() != null ||
                    newUser.getAccount_created() != null ||
                    newUser.getId() != null) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",newUser.toString());
                ThreadContext.put("responseBody","{\"Error Message\": \"Username, account_updated, account_created, and id fields should not be provided in the payload.\"}");
                log.warn("Invalid payload fields provided for user update.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Username, account_updated, account_created, and id fields should not be provided in the payload.\"}");
            }


            if (!isValidUpdateRequest(newUser)) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",newUser.toString());
                ThreadContext.put("responseBody","{\"Error Message\": \"Invalid update request.\"}");
                log.warn("Invalid update request fields provided.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Invalid update request.\"}");
            }
            if (newUser.getFirst_name() == null || newUser.getFirst_name().isEmpty()) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",newUser.toString());
                ThreadContext.put("responseBody","{\"Error Message\": \"First Name Field Cannot be Empty\"}");
                log.warn("First Name field cannot be Empty");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"First Name Field Cannot be Empty\"}");

            }

            if (newUser.getLast_name() == null || newUser.getLast_name().isEmpty()) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",newUser.toString());
                ThreadContext.put("responseBody","{\"Error Message\": \"Last Name Field Cannot be Empty\"}");
                log.warn("Last Name field cannot be Empty");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Last Name Field Cannot be Empty\"}");
            }

            if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {

                ThreadContext.put("severity", "WARNING");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                ThreadContext.put("RequestBody",newUser.toString());
                ThreadContext.put("responseBody","{\"Error Message\": \"Password Field Cannot be Empty\"}");
                log.warn("Password Field cannot be Empty");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"Error Message\": \"Password Field Cannot be Empty\"}");
            }


            updateUserDetails(user, newUser);


        user.setAccount_updated(LocalDateTime.now());
        UserRepo.save(user);

        ThreadContext.put("severity", "INFO");
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("path", request.getRequestURI());
        ThreadContext.put("RequestBody",user.toString());
        ThreadContext.put("responseBody","No Response Body returned here");

        log.info("User updated successfully.");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
        catch (Exception e) {

            ThreadContext.put("severity", "ERROR");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody",newUser.toString());
            ThreadContext.put("responseBody","No Response Body returned here");

            log.error("Error updating user: " + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
        finally {
            ThreadContext.clearAll();
        }

    }


    @RequestMapping(value = "/v1/user/self", method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Void> V1SelfInvalidMethod(HttpServletRequest request) {
        if (!DatabaseConnection.DatabaseConnectivity()) {
        
            ThreadContext.put("severity", "ERROR");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody","Request Body may or may not be given here");
            ThreadContext.put("responseBody","No Response Body returned here");

            log.error("Database connectivity issue. Service unavailable.");

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }

        ThreadContext.put("severity", "WARNING");
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("path", request.getRequestURI());
        ThreadContext.put("RequestBody","Request Body may or may not be given here");
        ThreadContext.put("responseBody","No Response Body returned here");

        log.warn("Method not Allowed.");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @RequestMapping(value = "/v1/user", method = {RequestMethod.GET, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Void> V1UserInvalidMethod(HttpServletRequest request) {
        if (!DatabaseConnection.DatabaseConnectivity()) {

            ThreadContext.put("severity", "ERROR");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            ThreadContext.put("RequestBody","Request Body may or may not be given here");
            ThreadContext.put("responseBody","No Response Body returned here");

            log.error("Database connectivity issue. Service unavailable.");

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }

        ThreadContext.put("severity", "WARNING");
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("path", request.getRequestURI());
        ThreadContext.put("RequestBody","Request Body may or may not be given here");
        ThreadContext.put("responseBody","No Response Body returned here");

        log.warn("Method not Allowed.");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    private static boolean IsValidPassword(String password) {
        
        ThreadContext.put("severity", "DEBUG");

        log.debug("Validating password format...");

        String regularExpression = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(regularExpression);
    }

    private boolean IsValidEmail(String email) {

        ThreadContext.put("severity", "DEBUG");
        log.debug("Validating password format...");
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




