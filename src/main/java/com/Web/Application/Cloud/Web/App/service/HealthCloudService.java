package com.Web.Application.Cloud.Web.App.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class HealthCloudService {
    @Autowired
    private JdbcTemplate jdbctemp;

    public boolean DatabaseConnectivity() {
        try {
            jdbctemp.queryForObject("SELECT 1", Integer.class);
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }

    public boolean PayloadRequest(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader("Content-Length")) && !request.getHeader("Content-Length").equals("0") || !request.getParameterMap().isEmpty();
    }
}
