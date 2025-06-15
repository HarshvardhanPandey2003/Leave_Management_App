package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.leave.LeaveRequestDto;
import com.example.leavemanagement.dto.leave.LeaveResponseDto;
import com.example.leavemanagement.dto.response.ApiResponse;
import com.example.leavemanagement.exception.InsufficientLeaveBalanceException;
import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaveService leaveService;

    private User testUser;
    private LeaveRequestDto leaveRequestDto;
    private LeaveRequest leaveRequest;

// BeforeEach is used to set up common test data before each test runs
// We basically create a user profile here with some initial leave balances and a leave request DTO
// We use Getters and Setters to manipulate the data as needed in tests
    @BeforeEach
    void setUp() {
        // Setup test user with leave balances
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@company.com");
        testUser.setRole(Role.EMPLOYEE);
        testUser.setAnnualLeaveBalance(20);
        testUser.setSickLeaveBalance(10);
        testUser.setCasualLeaveBalance(5);

        // Setup leave request DTO 
        //This is like filling out a leave application form - requesting 3 days of annual leave.
        leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setStartDate(LocalDate.now().plusDays(1));  // Tomorrow
        leaveRequestDto.setEndDate(LocalDate.now().plusDays(3)); // 3 days from now
        leaveRequestDto.setReason("Family vacation");
        leaveRequestDto.setLeaveType(LeaveType.ANNUAL);

        // Setup leave request entity
        // This is like creating a record of the leave request and storing it in the system
        leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setUser(testUser);
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setReason(leaveRequestDto.getReason());
        leaveRequest.setLeaveType(leaveRequestDto.getLeaveType());
        leaveRequest.setStatus(LeaveStatus.PENDING);
    }

    @Test
    @DisplayName("Should apply leave successfully with sufficient balance")
    void shouldApplyLeaveSuccessfully() {
        // Arrange
        // Here we replace the user repository's methods of getting data from the database with our custom responses
        // The functions mentioned below are the functions from the leave service that we are mocking
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        // This is where we call the actual method we want to test with the leave request DTO and user email
        LeaveResponseDto result = leaveService.applyLeave(leaveRequestDto, "john.doe@company.com");

        // Assert
        // Here we check how our test performed 
        // And assertEquals is used to check if the expected result matches the actual result
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getUserName());
        assertEquals(testUser.getEmail(), result.getUserEmail());
        assertEquals(17, testUser.getAnnualLeaveBalance()); // 20 - 3 days = 17
        
        // Here we chcek if the functions we mocked were called with the expected parameters
        // The mock functions were called properly, so we verify that
        verify(userRepository).save(testUser);
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient leave balance")
    void shouldThrowExceptionWhenInsufficientBalance() {
        // Arrange - Set very low balance
        testUser.setAnnualLeaveBalance(1);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        // Here we expect the method to throw an InsufficientLeaveBalanceException
        InsufficientLeaveBalanceException exception = assertThrows(
            InsufficientLeaveBalanceException.class,
            () -> leaveService.applyLeave(leaveRequestDto, "john.doe@company.com")
        );
        
        // Assert - Check the exception message
        assertEquals("Insufficient annual leave balance", exception.getMessage());
        // This 2 functions :
        // When your service fails due to insufficient balance, it doesn’t accidentally write anything to the database.
        // I expect that this method was called zero times when I use "never()"
        verify(leaveRequestRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should cancel pending leave request successfully")
    void shouldCancelLeaveRequestSuccessfully() {
        // Arrange
        leaveRequest.setStatus(LeaveStatus.PENDING);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        // Act
        ApiResponse response = leaveService.cancelLeaveRequest(1L, "john.doe@company.com");

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Leave request cancelled successfully", response.getMessage());
        assertEquals(LeaveStatus.CANCELLED, leaveRequest.getStatus());
        assertEquals(23, testUser.getAnnualLeaveBalance()); // Balance restored: 20 + 3 = 23
        
        verify(userRepository).save(testUser);
        verify(leaveRequestRepository).save(leaveRequest);
    }

    @Test
    @DisplayName("Should approve leave request successfully (admin functionality)")
    void shouldApproveLeaveRequestSuccessfully() {
        // Arrange
        leaveRequest.setStatus(LeaveStatus.PENDING);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        // Act
        ApiResponse response = leaveService.approveLeaveRequest(1L);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Leave request approved successfully", response.getMessage());
        assertEquals(LeaveStatus.APPROVED, leaveRequest.getStatus());
        
        verify(leaveRequestRepository).save(leaveRequest);
    }
}
