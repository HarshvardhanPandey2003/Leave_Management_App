package com.example.leavemanagement.dto.leave;

public class LeaveBalanceResponseDto {
    private int annualLeaveBalance;
    private int sickLeaveBalance;
    private int casualLeaveBalance;

    // Constructors
    public LeaveBalanceResponseDto() {}

    public LeaveBalanceResponseDto(int annualLeaveBalance, int sickLeaveBalance, int casualLeaveBalance) {
        this.annualLeaveBalance = annualLeaveBalance;
        this.sickLeaveBalance = sickLeaveBalance;
        this.casualLeaveBalance = casualLeaveBalance;
    }

    // Getters and Setters
    public int getAnnualLeaveBalance() {
        return annualLeaveBalance;
    }

    public void setAnnualLeaveBalance(int annualLeaveBalance) {
        this.annualLeaveBalance = annualLeaveBalance;
    }

    public int getSickLeaveBalance() {
        return sickLeaveBalance;
    }

    public void setSickLeaveBalance(int sickLeaveBalance) {
        this.sickLeaveBalance = sickLeaveBalance;
    }

    public int getCasualLeaveBalance() {
        return casualLeaveBalance;
    }

    public void setCasualLeaveBalance(int casualLeaveBalance) {
        this.casualLeaveBalance = casualLeaveBalance;
    }
}
