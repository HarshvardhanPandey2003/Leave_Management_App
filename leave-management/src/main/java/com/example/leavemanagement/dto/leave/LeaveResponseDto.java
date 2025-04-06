package com.example.leavemanagement.dto.leave;

import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.model.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveResponseDto {
    private Long id;
    private String userName;
    private String userEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveType leaveType;
    private LeaveStatus status;
    private LocalDateTime appliedAt;

    // Constructors
    public LeaveResponseDto() {}

    public LeaveResponseDto(Long id, String userName, String userEmail, 
                           LocalDate startDate, LocalDate endDate, String reason, 
                           LeaveType leaveType, LeaveStatus status, LocalDateTime appliedAt) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.leaveType = leaveType;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}
