package com.example.leavemanagement.dto.leave;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeaveBalanceUpdateDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Annual leave balance is required")
    @Min(value = 0, message = "Leave balance cannot be negative")
    private Integer annualLeaveBalance;
    
    @NotNull(message = "Sick leave balance is required")
    @Min(value = 0, message = "Leave balance cannot be negative")
    private Integer sickLeaveBalance;
    
    @NotNull(message = "Casual leave balance is required")
    @Min(value = 0, message = "Leave balance cannot be negative")
    private Integer casualLeaveBalance;
    
    // Constructors
    public LeaveBalanceUpdateDto() {}
    
    public LeaveBalanceUpdateDto(String email, Integer annualLeaveBalance, 
                               Integer sickLeaveBalance, Integer casualLeaveBalance) {
        this.email = email;
        this.annualLeaveBalance = annualLeaveBalance;
        this.sickLeaveBalance = sickLeaveBalance;
        this.casualLeaveBalance = casualLeaveBalance;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getAnnualLeaveBalance() {
        return annualLeaveBalance;
    }
    
    public void setAnnualLeaveBalance(Integer annualLeaveBalance) {
        this.annualLeaveBalance = annualLeaveBalance;
    }
    
    public Integer getSickLeaveBalance() {
        return sickLeaveBalance;
    }
    
    public void setSickLeaveBalance(Integer sickLeaveBalance) {
        this.sickLeaveBalance = sickLeaveBalance;
    }
    
    public Integer getCasualLeaveBalance() {
        return casualLeaveBalance;
    }
    
    public void setCasualLeaveBalance(Integer casualLeaveBalance) {
        this.casualLeaveBalance = casualLeaveBalance;
    }
}
