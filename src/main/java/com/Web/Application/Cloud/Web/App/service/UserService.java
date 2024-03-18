package com.Web.Application.Cloud.Web.App.service;

import com.Web.Application.Cloud.Web.App.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService {
    List<User> FetchUserInformation();

    User CreatingUser(User newUser) throws Exception;

    boolean AreValidCredentials(String username, String password);



}
