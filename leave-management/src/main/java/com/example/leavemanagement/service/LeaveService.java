package com.example.leavemanagement.service;
import com.example.leavemanagement.dto.leave.*;
import com.example.leavemanagement.dto.leave.LeaveRequestDto;
import com.example.leavemanagement.dto.leave.LeaveResponseDto;
import com.example.leavemanagement.dto.response.ApiResponse;
import com.example.leavemanagement.exception.InsufficientLeaveBalanceException;
import com.example.leavemanagement.exception.InvalidLeaveDatesException;
import com.example.leavemanagement.exception.ResourceNotFoundException;
import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.model.User;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Apply for a leave
     */
    public LeaveResponseDto applyLeave(LeaveRequestDto leaveRequestDto, String userEmail) {
        // Validate request
        validateLeaveRequest(leaveRequestDto);

        // Get current user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        // Calculate number of days
        long numberOfDays = java.time.temporal.ChronoUnit.DAYS.between(
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate().plusDays(1)
        );

        // Check leave balance - FIXED: Changed Type() to getLeaveType()
        switch (leaveRequestDto.getLeaveType()) {
            case ANNUAL:
                if (user.getAnnualLeaveBalance() < numberOfDays) {
                    throw new InsufficientLeaveBalanceException("Insufficient annual leave balance");
                }
                user.setAnnualLeaveBalance(user.getAnnualLeaveBalance() - (int)numberOfDays);
                break;

            case SICK:
                if (user.getSickLeaveBalance() < numberOfDays) {
                    throw new InsufficientLeaveBalanceException("Insufficient sick leave balance");
                }
                user.setSickLeaveBalance(user.getSickLeaveBalance() - (int)numberOfDays);
                break;

            case CASUAL:
                if (user.getCasualLeaveBalance() < numberOfDays) {
                    throw new InsufficientLeaveBalanceException("Insufficient casual leave balance");
                }
                user.setCasualLeaveBalance(user.getCasualLeaveBalance() - (int)numberOfDays);
                break;

            default:
                // For other leave types, no balance check
                break;
        }

        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest(
                user,
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate(),
                leaveRequestDto.getReason(),
                leaveRequestDto.getLeaveType()
        );

        // Save the user with updated balance and the leave request
        userRepository.save(user);
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        // Return response
        return convertToDto(savedRequest);
    }

    /**
     * Cancel a pending leave request
     */
    public ApiResponse cancelLeaveRequest(Long leaveId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        // Check if the leave request belongs to the user
        if (!leaveRequest.getUser().getId().equals(user.getId())) {
            return new ApiResponse(false, "You can only cancel your own leave requests");
        }

        // Check if the leave request is still pending
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            return new ApiResponse(false, "Only pending leave requests can be cancelled");
        }

        // Calculate number of days
        long numberOfDays = java.time.temporal.ChronoUnit.DAYS.between(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate().plusDays(1)
        );

        // Restore leave balance
        switch (leaveRequest.getLeaveType()) {
            case ANNUAL:
                user.setAnnualLeaveBalance(user.getAnnualLeaveBalance() + (int)numberOfDays);
                break;

            case SICK:
                user.setSickLeaveBalance(user.getSickLeaveBalance() + (int)numberOfDays);
                break;

            case CASUAL:
                user.setCasualLeaveBalance(user.getCasualLeaveBalance() + (int)numberOfDays);
                break;

            default:
                // For other leave types, no balance update
                break;
        }

        // Cancel the leave request
        leaveRequest.setStatus(LeaveStatus.CANCELLED);

        // Save the updated user and leave request
        userRepository.save(user);
        leaveRequestRepository.save(leaveRequest);

        return new ApiResponse(true, "Leave request cancelled successfully");
    }

    /**
     * Get all leave requests (for admin/HR)
     */
    // Use of stream is when we have to take a list based on some filter and then return it as a response
    public List<LeaveResponseDto> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByOrderByAppliedAtDesc();

        return leaveRequests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert LeaveRequest entity to LeaveResponseDto
     */
    // We have a separate method to convert LeaveRequest entity to LeaveResponseDto
    // For APIs like getAllLeaveRequests() and getMyLeaveRequests() where we have to return a list of leave requests
    private LeaveResponseDto convertToDto(LeaveRequest leaveRequest) {
        return new LeaveResponseDto(
                leaveRequest.getId(),
                leaveRequest.getUser().getName(),
                leaveRequest.getUser().getEmail(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getReason(),
                leaveRequest.getLeaveType(),
                leaveRequest.getStatus(),
                leaveRequest.getAppliedAt()
        );
    }

    /**
     * Validate leave request
     */
    private void validateLeaveRequest(LeaveRequestDto leaveRequestDto) {
        // Check if end date is after start date
        if (leaveRequestDto.getEndDate().isBefore(leaveRequestDto.getStartDate())) {
            throw new InvalidLeaveDatesException("End date cannot be before start date");
        }

        // Check if dates are in the past
        if (leaveRequestDto.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidLeaveDatesException("Cannot apply for leave with start date in the past");
        }
    }

    public ApiResponse approveLeaveRequest(Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        // Check if the leave request is in a state that can be approved
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            return new ApiResponse(false, "Only pending leave requests can be approved");
        }

        // Approve the leave request
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepository.save(leaveRequest);

        return new ApiResponse(true, "Leave request approved successfully");
    }

    /**
     * Reject a leave request (for admin/HR)
     */
    public ApiResponse rejectLeaveRequest(Long leaveId, String rejectionReason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        // Check if the leave request is in a state that can be rejected
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            return new ApiResponse(false, "Only pending leave requests can be rejected");
        }

        // Reject the leave request
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        // If you want to store the rejection reason, add a field to the LeaveRequest entity
        // For now, we're not storing it
        leaveRequestRepository.save(leaveRequest);

        return new ApiResponse(true, "Leave request rejected successfully");
    }

    public List<LeaveResponseDto> getMyLeaveRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserOrderByAppliedAtDesc(user);

        return leaveRequests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LeaveBalanceResponseDto getLeaveBalance(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        return new LeaveBalanceResponseDto(
                user.getAnnualLeaveBalance(),
                user.getSickLeaveBalance(),
                user.getCasualLeaveBalance()
        );
    }

    /**
     * Update leave balance for a user (admin only)
     */
    public ApiResponse updateLeaveBalance(LeaveBalanceUpdateDto balanceUpdateDto) {
        User user = userRepository.findByEmail(balanceUpdateDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + balanceUpdateDto.getEmail()));
        // Here we take the input of all the parameters from the LeaveBalanceUpdateDto
        // Then use getters to get the values and setters to set the values in the user object
        user.setAnnualLeaveBalance(balanceUpdateDto.getAnnualLeaveBalance());
        user.setSickLeaveBalance(balanceUpdateDto.getSickLeaveBalance());
        user.setCasualLeaveBalance(balanceUpdateDto.getCasualLeaveBalance());
        // Then save it to the database
        userRepository.save(user);

        return new ApiResponse(true, "Leave balance updated successfully");
    }
}
