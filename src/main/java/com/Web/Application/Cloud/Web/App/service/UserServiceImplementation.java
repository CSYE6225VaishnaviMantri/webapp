package com.Web.Application.Cloud.Web.App.service;

import com.Web.Application.Cloud.Web.App.entity.User;
import com.Web.Application.Cloud.Web.App.repository.UserRepository;
import com.Web.Application.Cloud.Web.App.util.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository UserRepo;
    private PasswordEncoder passwordencoding;


    public UserServiceImplementation(UserRepository UserRepo) {
        this.UserRepo = UserRepo;
        this.passwordencoding = new BCryptPasswordEncoder();
    }

    @Override
    public List<User> FetchUserInformation() {
        return UserRepo.findAll();
    }

    @Override
    public User CreatingUser(User newUser) throws Exception {
        String username = newUser.getUsername();

        if (UserRepo.findByUsername(username) != null) {

            System.out.println("User with username " + username + " already exists.");
            throw new Exception("User Name ");
        }
        else {
            String encodepass = this.passwordencoding.encode(newUser.getPassword());
            newUser.setPassword(encodepass);
            System.out.println("User with username " + username + " created successfully.");
            return UserRepo.save(newUser);
        }
    }


    public boolean AreValidCredentials(String username, String password) {

        User user = UserRepo.findByUsername(username);
        return this.passwordencoding.matches(password, user.getPassword());
    }



}















