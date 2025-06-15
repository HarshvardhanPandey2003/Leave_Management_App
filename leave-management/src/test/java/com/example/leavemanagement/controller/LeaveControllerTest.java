package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.leave.LeaveRequestDto;
import com.example.leavemanagement.dto.leave.LeaveResponseDto;
import com.example.leavemanagement.dto.response.ApiResponse;
import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.model.LeaveType;
import com.example.leavemanagement.security.JwtUtils;
import com.example.leavemanagement.security.UserDetailsServiceImpl;
import com.example.leavemanagement.service.LeaveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaveController.class)
@EnableMethodSecurity
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveService leaveService;
    
    @MockitoBean
    private JwtUtils jwtUtils;
    
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully apply for leave with authenticated employee")
    @WithMockUser(username = "employee@company.com", roles = "EMPLOYEE")
    void shouldApplyLeaveSuccessfully() throws Exception {
        // Arrange
        LeaveRequestDto leaveRequest = new LeaveRequestDto();
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(3));
        leaveRequest.setReason("Vacation");
        leaveRequest.setLeaveType(LeaveType.ANNUAL);
        
        LeaveResponseDto mockResponse = createMockLeaveResponse(1L, LeaveStatus.PENDING, "employee@company.com");
        
        when(leaveService.applyLeave(any(LeaveRequestDto.class), anyString()))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/leave/apply")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(leaveRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userEmail").value("employee@company.com"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.leaveType").value("ANNUAL"));
    }

    @Test
    @DisplayName("Should return validation errors when applying leave with invalid data")
    @WithMockUser(username = "employee@company.com", roles = "EMPLOYEE")
    void shouldReturnValidationErrorsForInvalidLeaveRequest() throws Exception {
        // Arrange - Create invalid leave request (missing required fields)
        LeaveRequestDto invalidRequest = new LeaveRequestDto();
        // Leave all fields null/empty to trigger validation errors
        
        // Act & Assert
        mockMvc.perform(post("/api/leave/apply")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should allow employee to retrieve their own leave requests")
    @WithMockUser(username = "employee@company.com", roles = "EMPLOYEE")
    void shouldAllowEmployeeToGetOwnLeaveRequests() throws Exception {
        // Arrange
        List<LeaveResponseDto> mockEmployeeRequests = Arrays.asList(
                createMockLeaveResponse(1L, LeaveStatus.PENDING, "employee@company.com"),
                createMockLeaveResponse(2L, LeaveStatus.APPROVED, "employee@company.com")
        );
        
        when(leaveService.getMyLeaveRequests(anyString())).thenReturn(mockEmployeeRequests);

        // Act & Assert
        mockMvc.perform(get("/api/leave/my-requests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userEmail").value("employee@company.com"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    @DisplayName("Should allow admin to get all leave requests")
    @WithMockUser(username = "admin@company.com", roles = "ADMIN") 
    void shouldAllowAdminToGetAllLeaveRequests() throws Exception {
        // Arrange
        List<LeaveResponseDto> mockAllRequests = Arrays.asList(
                createMockLeaveResponse(1L, LeaveStatus.PENDING, "user1@company.com"),
                createMockLeaveResponse(2L, LeaveStatus.APPROVED, "user2@company.com"),
                createMockLeaveResponse(3L, LeaveStatus.REJECTED, "user3@company.com")
        );
        
        when(leaveService.getAllLeaveRequests()).thenReturn(mockAllRequests);

        // Act & Assert
        mockMvc.perform(get("/api/leave/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].status").value("APPROVED"))
                .andExpect(jsonPath("$[2].status").value("REJECTED"));
    }

    @Test
    @DisplayName("Should handle admin leave approval with proper error responses")
    @WithMockUser(username = "admin@company.com", roles = "ADMIN")
    void shouldHandleLeaveApprovalWithErrorScenarios() throws Exception {
        // Arrange - Mock service to return failure response
        ApiResponse failureResponse = new ApiResponse(false, "Only pending leave requests can be approved");
        when(leaveService.approveLeaveRequest(1L)).thenReturn(failureResponse);
        
        ApiResponse successResponse = new ApiResponse(true, "Leave request approved successfully");
        when(leaveService.approveLeaveRequest(2L)).thenReturn(successResponse);

        // Act & Assert - Test failure scenario
        mockMvc.perform(put("/api/leave/approve/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Only pending leave requests can be approved"));

        // Act & Assert - Test success scenario
        mockMvc.perform(put("/api/leave/approve/2")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Leave request approved successfully"));
    }

    @Test
    @DisplayName("Should deny access to employee trying to access admin endpoints")
    @WithMockUser(username = "employee@company.com", roles = "EMPLOYEE")
    void shouldDenyEmployeeAccessToAdminEndpoints() throws Exception {
        // Act & Assert - Test admin-only endpoint access denial
        mockMvc.perform(get("/api/leave/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Test another admin endpoint
        mockMvc.perform(put("/api/leave/approve/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Test reject endpoint
        mockMvc.perform(put("/api/leave/reject/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow employee to cancel their own pending leave request")
    @WithMockUser(username = "employee@company.com", roles = "EMPLOYEE")
    void shouldAllowEmployeeToCancelOwnLeaveRequest() throws Exception {
        // Arrange
        ApiResponse successResponse = new ApiResponse(true, "Leave request cancelled successfully");
        when(leaveService.cancelLeaveRequest(1L, "employee@company.com")).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(put("/api/leave/cancel/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Leave request cancelled successfully"));
    }

    // Helper method to create mock LeaveResponseDto with complete data
    private LeaveResponseDto createMockLeaveResponse(Long id, LeaveStatus status, String userEmail) {
        LeaveResponseDto response = new LeaveResponseDto();
        response.setId(id);
        response.setUserName("Test User");
        response.setUserEmail(userEmail);
        response.setStartDate(LocalDate.now().plusDays(1));
        response.setEndDate(LocalDate.now().plusDays(3));
        response.setReason("Test reason");
        response.setLeaveType(LeaveType.ANNUAL);
        response.setStatus(status);
        response.setAppliedAt(LocalDateTime.now());
        return response;
    }
}
