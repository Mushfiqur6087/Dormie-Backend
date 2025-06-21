package com.HMS.hms.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.DTO.JwtResponse;
import com.HMS.hms.DTO.LoginRequest;
import com.HMS.hms.DTO.MessageResponse;
import com.HMS.hms.DTO.SignupRequest;
import com.HMS.hms.Security.JwtUtils;
import com.HMS.hms.Security.UserDetailsImpl;
import com.HMS.hms.Service.UserService;
import com.HMS.hms.enums.UserRole;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    /**
     * Admin-only endpoint for creating new users.
     * Requires authentication via JWT token with ADMIN role.
     * Secured with both @PreAuthorize annotation and manual role checking.
     */

    @PostMapping("admin/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        
        // Additional check to ensure current user is admin
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth == null || !currentAuth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Authentication required!"));
        }
        
        // Check if current user has ADMIN role
        boolean isAdmin = currentAuth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Error: Only administrators can create new users!"));
        }

        String strRole = signUpRequest.getRole();
        
        // Use the UserRole enum for consistent role handling
        UserRole userRole = UserRole.fromString(strRole);
        
        // Check if admin is trying to create a student
        if (userRole != UserRole.STUDENT) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Admin can only create STUDENT users!"));
        }
        
        try {
            // Create student user using service layer
            userService.createStudentUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("Student user registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}

