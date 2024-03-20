package com.Web.Application.Cloud.Web.App.controller;

import com.Web.Application.Cloud.Web.App.service.HealthCloudService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
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

    private static final Logger log = LogManager.getLogger(HealthCloudController.class);
    
    @GetMapping("/healthz")
    public ResponseEntity<Void> HealthAPICheck(HttpServletResponse response, HttpServletRequest request) {

        
        if (HCS.PayloadRequest(request)) {

            ThreadContext.put("severity", "WARNING");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            log.warn("Invalid payload received. Returning Bad Request.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        try {

            boolean databaseConnected = HCS.DatabaseConnectivity();

            if (databaseConnected) {

                ThreadContext.put("severity", "INFO");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                log.info("Database connectivity check successful,Health check API endpoint accessed.");

                return ResponseEntity.ok()
                        .headers(DefiningHttpHeaders())
                        .build();
            }
            else {

                ThreadContext.put("severity", "ERROR");
                ThreadContext.put("httpMethod", request.getMethod());
                ThreadContext.put("path", request.getRequestURI());
                log.error("Database connectivity check failed. Service unavailable.");
                
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .headers(DefiningHttpHeaders())
                        .build();
            }
        } 
        catch (Exception e) {

            ThreadContext.put("severity", "ERROR");
            ThreadContext.put("httpMethod", request.getMethod());
            ThreadContext.put("path", request.getRequestURI());
            log.error("An error occurred during health check.", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noCache())
                    .headers(DefiningHttpHeaders())
                    .build();
        }

    }

    @RequestMapping(value = "/healthz", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH,RequestMethod.HEAD,RequestMethod.OPTIONS,RequestMethod.TRACE})
    public ResponseEntity<Void> InvalidMethod(HttpServletRequest request) {

        ThreadContext.put("severity", "WARNING");
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("path", request.getRequestURI());
        log.warn("Invalid HTTP method used for health check.");
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @RequestMapping(value = "/**", method = {RequestMethod.GET,RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH,RequestMethod.HEAD,RequestMethod.OPTIONS,RequestMethod.TRACE})
    public ResponseEntity<Void> InvalidURLMethod(HttpServletRequest request) {

        ThreadContext.put("severity", "WARNING");
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("path", request.getRequestURI());
        log.warn("Invalid URL accessed.");

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
