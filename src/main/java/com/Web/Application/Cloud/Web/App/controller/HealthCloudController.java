package com.Web.Application.Cloud.Web.App.controller;

import com.Web.Application.Cloud.Web.App.service.HealthCloudService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCloudController {
    @Autowired
    private HealthCloudService HCS;

    @GetMapping("/healthz")
    public ResponseEntity<Void> HealthAPICheck(HttpServletResponse response, HttpServletRequest request) {

        if (HCS.PayloadRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        try {

            boolean databaseConnected = HCS.DatabaseConnectivity();

            if (databaseConnected) {
                return ResponseEntity.ok()
                        .headers(DefiningHttpHeaders())
                        .build();
            } else {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .headers(DefiningHttpHeaders())
                        .build();
            }
        } catch (Exception e) {
            // Log the exception if needed
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noCache())
                    .headers(DefiningHttpHeaders())
                    .build();
        }

    }

    @RequestMapping(value = "/healthz", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH,RequestMethod.HEAD,RequestMethod.OPTIONS,RequestMethod.TRACE})
    public ResponseEntity<Void> InvalidMethod(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @RequestMapping("/**")
    public ResponseEntity<Void> InvalidURLMethod() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    private HttpHeaders DefiningHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-cache, no-store, must-revalidate;");
        headers.setPragma("no-cache");
        headers.set("X-Content-Type-Options", "nosniff");
        return headers;
    }
}
