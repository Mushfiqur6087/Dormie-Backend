package com.HMS.hms.Controller.Test;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController("authTestController")
@RequestMapping("/api/auth-test")
public class TestController {
    
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/manager")
    @PreAuthorize("isAuthenticated()")
    public String managerAccess() {
        return "Hall Manager Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("isAuthenticated()")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/authority")
    @PreAuthorize("isAuthenticated()")
    public String authorityAccess() {
        return "Authority Board.";
    }

    @GetMapping("/supervisor")
    @PreAuthorize("isAuthenticated()")
    public String supervisorAccess() {
        return "Supervisor Board.";
    }
}
