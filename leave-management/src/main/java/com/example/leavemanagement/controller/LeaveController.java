package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.leave.LeaveRequestDto;
import com.example.leavemanagement.dto.leave.LeaveResponseDto;
import com.example.leavemanagement.dto.leave.RejectionRequestDto;
import com.example.leavemanagement.dto.leave.LeaveBalanceResponseDto;
import com.example.leavemanagement.dto.leave.LeaveBalanceUpdateDto;
import com.example.leavemanagement.dto.response.ApiResponse;
import com.example.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    /**
     * Apply for leave
     */
    @PostMapping("/apply")
    public ResponseEntity<LeaveResponseDto> applyLeave(@Valid @RequestBody LeaveRequestDto leaveRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        LeaveResponseDto responseDto = leaveService.applyLeave(leaveRequestDto, currentUserEmail);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Get my leave requests
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<LeaveResponseDto>> getMyLeaveRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        List<LeaveResponseDto> leaveRequests = leaveService.getMyLeaveRequests(currentUserEmail);
        return ResponseEntity.ok(leaveRequests);
    }

    /**
     * Cancel leave request
     */
    @PutMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse> cancelLeaveRequest(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        ApiResponse response = leaveService.cancelLeaveRequest(id, currentUserEmail);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get all leave requests (admin only)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaveResponseDto>> getAllLeaveRequests() {
        List<LeaveResponseDto> leaveRequests = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(leaveRequests);
    }

    /**
     * Approve a leave request.
     * Endpoint: PUT /api/leave/approve/{id}
     * Only accessible by ADMIN users.
     */
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> approveLeaveRequest(@PathVariable Long id) {
        ApiResponse response = leaveService.approveLeaveRequest(id);
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Reject a leave request.
     * Endpoint: PUT /api/leave/reject/{id}
     * Only accessible by ADMIN users.
     * Accepts an optional body with a rejection reason.
     */
    @PutMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> rejectLeaveRequest(
            @PathVariable Long id,
            @RequestBody(required = false) RejectionRequestDto rejectionRequest) {
        String reason = (rejectionRequest != null) ? rejectionRequest.getReason() : null;
        ApiResponse response = leaveService.rejectLeaveRequest(id, reason);
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/balance")
    public ResponseEntity<LeaveBalanceResponseDto> getLeaveBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        LeaveBalanceResponseDto balanceResponseDto = leaveService.getLeaveBalance(currentUserEmail);
        return ResponseEntity.ok(balanceResponseDto);
    }

    /**
     * Update leave balance (admin only)
     */
    @PutMapping("/balance/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateLeaveBalance(@Valid @RequestBody LeaveBalanceUpdateDto balanceUpdateDto) {
        ApiResponse response = leaveService.updateLeaveBalance(balanceUpdateDto);
        return ResponseEntity.ok(response);
    }
}
