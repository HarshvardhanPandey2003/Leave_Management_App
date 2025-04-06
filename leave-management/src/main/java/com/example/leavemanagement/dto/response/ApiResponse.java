package com.example.leavemanagement.dto.response;

public class ApiResponse {
    private Boolean success;
    private String message;

    // No-argument constructor
    public ApiResponse() {}

    // All-argument constructor
    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getter for 'success'
    public Boolean getSuccess() {
        return success;
    }

    // Setter for 'success'
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    // Getter for 'message'
    public String getMessage() {
        return message;
    }

    // Setter for 'message'
    public void setMessage(String message) {
        this.message = message;
    }
}
