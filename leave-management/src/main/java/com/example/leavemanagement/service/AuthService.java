package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.request.LoginRequest;
import com.example.leavemanagement.dto.request.SignupRequest;
import com.example.leavemanagement.dto.response.ApiResponse;
import com.example.leavemanagement.dto.response.JwtResponse;
import com.example.leavemanagement.model.User;
import com.example.leavemanagement.repository.UserRepository;
import com.example.leavemanagement.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
   
    @Autowired
    private UserRepository userRepository;
   
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
   
    public ApiResponse registerUser(SignupRequest signupRequest) {
        // Check if email is already taken
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new ApiResponse(false, "Email is already in use!");
        }
       
        // Create new user
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole());
       
        userRepository.save(user);
       
        return new ApiResponse(true, "User registered successfully!");
    }
    
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user details
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT token
            String jwt = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
            
            // Return JWT response
            return new JwtResponse(
                jwt,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
