package com.keviny.customeridentity.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

    @Test
    @DisplayName("Should create error response with message and details")
    void shouldCreateErrorResponseWithMessageAndDetails() {
        // Given
        String message = "Validation Failed";
        Map<String, String> details = new HashMap<>();
        details.put("firstName", "First name cannot be blank");
        details.put("lastName", "Last name cannot be blank");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertNotNull(errorResponse);
        assertEquals(message, errorResponse.getMessage());
        assertEquals(details, errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Should create error response with empty details")
    void shouldCreateErrorResponseWithEmptyDetails() {
        // Given
        String message = "No specific errors";
        Map<String, String> details = new HashMap<>();

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertNotNull(errorResponse);
        assertEquals(message, errorResponse.getMessage());
        assertTrue(errorResponse.getDetails().isEmpty());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should create error response with null message")
    void shouldCreateErrorResponseWithNullMessage() {
        // Given
        String message = null;
        Map<String, String> details = new HashMap<>();
        details.put("error", "Some error occurred");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertNotNull(errorResponse);
        assertNull(errorResponse.getMessage());
        assertEquals(details, errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should create error response with null details")
    void shouldCreateErrorResponseWithNullDetails() {
        // Given
        String message = "Error occurred";
        Map<String, String> details = null;

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertNotNull(errorResponse);
        assertEquals(message, errorResponse.getMessage());
        assertNull(errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should preserve timestamp between creation and access")
    void shouldPreserveTimestampBetweenCreationAndAccess() {
        // Given
        String message = "Test message";
        Map<String, String> details = new HashMap<>();
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // When
        LocalDateTime firstAccess = errorResponse.getTimestamp();
        
        // Small delay to ensure time passes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        LocalDateTime secondAccess = errorResponse.getTimestamp();

        // Then
        assertEquals(firstAccess, secondAccess);
    }

    @Test
    @DisplayName("Should handle single validation error")
    void shouldHandleSingleValidationError() {
        // Given
        String message = "Validation Failed";
        Map<String, String> details = new HashMap<>();
        details.put("ssn", "SSN cannot be blank");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertEquals(1, errorResponse.getDetails().size());
        assertEquals("SSN cannot be blank", errorResponse.getDetails().get("ssn"));
    }

    @Test
    @DisplayName("Should handle multiple validation errors")
    void shouldHandleMultipleValidationErrors() {
        // Given
        String message = "Multiple Validation Errors";
        Map<String, String> details = new HashMap<>();
        details.put("firstName", "First name cannot be blank");
        details.put("lastName", "Last name cannot be blank");
        details.put("ssn", "SSN cannot be blank");
        details.put("dob", "Date of birth must be in the past");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertEquals(4, errorResponse.getDetails().size());
        assertEquals("First name cannot be blank", errorResponse.getDetails().get("firstName"));
        assertEquals("Last name cannot be blank", errorResponse.getDetails().get("lastName"));
        assertEquals("SSN cannot be blank", errorResponse.getDetails().get("ssn"));
        assertEquals("Date of birth must be in the past", errorResponse.getDetails().get("dob"));
    }

    @Test
    @DisplayName("Should handle business logic error")
    void shouldHandleBusinessLogicError() {
        // Given
        String message = "Business Logic Error";
        Map<String, String> details = new HashMap<>();
        details.put("error", "Customer with this SSN already exists");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertEquals("Business Logic Error", errorResponse.getMessage());
        assertEquals("Customer with this SSN already exists", errorResponse.getDetails().get("error"));
    }

    @Test
    @DisplayName("Should store reference to details map")
    void shouldStoreReferenceToDetailsMap() {
        // Given
        String message = "Test message";
        Map<String, String> details = new HashMap<>();
        details.put("field", "error");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);
        
        // Modify the original details map
        details.put("newField", "newError");

        // Then
        // The error response contains a reference to the same map
        assertEquals(2, errorResponse.getDetails().size());
        assertEquals("error", errorResponse.getDetails().get("field"));
        assertEquals("newError", errorResponse.getDetails().get("newField"));
    }

    @Test
    @DisplayName("Should handle empty string message")
    void shouldHandleEmptyStringMessage() {
        // Given
        String message = "";
        Map<String, String> details = new HashMap<>();
        details.put("info", "Empty message test");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertEquals("", errorResponse.getMessage());
        assertEquals("Empty message test", errorResponse.getDetails().get("info"));
    }

    @Test
    @DisplayName("Should handle complex error details")
    void shouldHandleComplexErrorDetails() {
        // Given
        String message = "Complex Validation Error";
        Map<String, String> details = new HashMap<>();
        details.put("customer.address.street", "Street cannot be blank");
        details.put("customer.phone", "Phone number format is invalid");
        details.put("customer.email", "Email format is invalid");

        // When
        ErrorResponse errorResponse = new ErrorResponse(message, details);

        // Then
        assertEquals(3, errorResponse.getDetails().size());
        assertEquals("Street cannot be blank", errorResponse.getDetails().get("customer.address.street"));
        assertEquals("Phone number format is invalid", errorResponse.getDetails().get("customer.phone"));
        assertEquals("Email format is invalid", errorResponse.getDetails().get("customer.email"));
    }
}