package com.HMS.hms.Controller.Test;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('STUDENT') or hasRole('HALL_MANAGER') or hasRole('ADMIN') or hasRole('AUTHORITY') or hasRole('SUPERVISOR')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('HALL_MANAGER')")
    public String managerAccess() {
        return "Hall Manager Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/authority")
    @PreAuthorize("hasRole('AUTHORITY')")
    public String authorityAccess() {
        return "Authority Board.";
    }

    @GetMapping("/supervisor")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public String supervisorAccess() {
        return "Supervisor Board.";
    }
}
