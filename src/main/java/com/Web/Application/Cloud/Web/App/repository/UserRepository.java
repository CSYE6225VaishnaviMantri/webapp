package com.Web.Application.Cloud.Web.App.repository;

import com.Web.Application.Cloud.Web.App.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public User findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);
    }


