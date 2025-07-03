package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.request.LoginRequest;
import com.example.leavemanagement.dto.request.SignupRequest;
import com.example.leavemanagement.dto.response.ApiResponse;
import com.example.leavemanagement.dto.response.JwtResponse;
import com.example.leavemanagement.model.Role;
import com.example.leavemanagement.security.JwtUtils;
import com.example.leavemanagement.security.UserDetailsServiceImpl;
import com.example.leavemanagement.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully register user with valid signup data")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("John Doe");
        signupRequest.setEmail("john.doe@company.com");
        signupRequest.setPassword("password123");
        signupRequest.setRole(Role.EMPLOYEE);

        ApiResponse successResponse = new ApiResponse(true, "User registered successfully!");
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("Should authenticate user and return JWT token")
    void shouldAuthenticateUserAndReturnToken() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@company.com");
        loginRequest.setPassword("password123");

        JwtResponse jwtResponse = new JwtResponse(
                "eyJhbGciOiJIUzI1NiJ9.mock-token",
                1L,
                "John Doe",
                "john.doe@company.com",
                "EMPLOYEE"
        );
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9.mock-token"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@company.com"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    @DisplayName("Should return 400 when signup validation fails")
    void shouldReturnBadRequestOnSignupValidationFailure() throws Exception {
        // Arrange - Invalid signup request with validation errors
        SignupRequest invalidRequest = new SignupRequest();
        invalidRequest.setName(""); // Blank name
        invalidRequest.setEmail("invalid-email"); // Invalid email format
        invalidRequest.setPassword("123"); // Password too short
        invalidRequest.setRole(null); // Null role

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when email already exists")
    void shouldReturnBadRequestWhenEmailAlreadyExists() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("John Doe");
        signupRequest.setEmail("existing@company.com");
        signupRequest.setPassword("password123");
        signupRequest.setRole(Role.EMPLOYEE);

        ApiResponse failureResponse = new ApiResponse(false, "Email is already in use!");
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(failureResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email is already in use!"));
    }

    @Test
    @DisplayName("Should return 400 when login validation fails")
    void shouldReturnBadRequestOnLoginValidationFailure() throws Exception {
        // Arrange - Invalid login request
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail(""); // Blank email
        invalidRequest.setPassword(""); // Blank password

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle authentication failure gracefully")
    void shouldHandleAuthenticationFailure() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrong@company.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());
    }
}
