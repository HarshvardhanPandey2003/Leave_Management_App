package com.example.leavemanagement.dto.leave;

public class RejectionRequestDto {
    private String reason;
    
    // Constructors
    public RejectionRequestDto() {}
    
    public RejectionRequestDto(String reason) {
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
