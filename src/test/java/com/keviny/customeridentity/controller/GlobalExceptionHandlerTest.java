package com.keviny.customeridentity.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.keviny.customeridentity.dto.ErrorResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void handleValidationExceptions_handlesMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("customerDto", "firstName", "First name cannot be blank");
        FieldError fieldError2 = new FieldError("customerDto", "ssn", "SSN cannot be blank");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().getMessage());
        
        Map<String, String> details = response.getBody().getDetails();
        assertEquals("First name cannot be blank", details.get("firstName"));
        assertEquals("SSN cannot be blank", details.get("ssn"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty field errors")
    void handleValidationExceptions_handlesEmptyFieldErrors() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().isEmpty());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with constraint violations")
    void handleConstraintViolationException_handlesConstraintViolations() {
        // Given
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);
        
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("must be positive");
        when(path1.toString()).thenReturn("id");
        
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("size must be between 1 and 32");
        when(path2.toString()).thenReturn("ssn");
        
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        ConstraintViolationException ex = new ConstraintViolationException("Constraint violations", violations);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().getMessage());
        
        Map<String, String> details = response.getBody().getDetails();
        assertEquals("must be positive", details.get("id"));
        assertEquals("size must be between 1 and 32", details.get("ssn"));
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with empty violations")
    void handleConstraintViolationException_handlesEmptyViolations() {
        // Given
        Set<ConstraintViolation<?>> violations = Set.of();
        ConstraintViolationException ex = new ConstraintViolationException("No violations", violations);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().isEmpty());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void handleIllegalArgumentException_handlesIllegalArgumentException() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid customer data");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Business Logic Error", response.getBody().getMessage());
        
        Map<String, String> details = response.getBody().getDetails();
        assertEquals("Invalid customer data", details.get("error"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with null message")
    void handleIllegalArgumentException_handlesNullMessage() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException((String) null);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Business Logic Error", response.getBody().getMessage());
        
        Map<String, String> details = response.getBody().getDetails();
        assertNull(details.get("error"));
    }

    @Test
    @DisplayName("Should handle multiple field errors with same field name")
    void handleValidationExceptions_handlesMultipleErrorsSameField() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("customerDto", "firstName", "First name cannot be blank");
        FieldError fieldError2 = new FieldError("customerDto", "firstName", "First name too long");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> details = response.getBody().getDetails();
        // Should contain the last error message for the field
        assertEquals("First name too long", details.get("firstName"));
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with complex property paths")
    void handleConstraintViolationException_handlesComplexPropertyPaths() {
        // Given
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("validation failed");
        when(path.toString()).thenReturn("customer.address.zipCode");
        
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException ex = new ConstraintViolationException("Complex path violation", violations);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> details = response.getBody().getDetails();
        assertEquals("validation failed", details.get("customer.address.zipCode"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with nested object errors")
    void handleValidationExceptions_handlesNestedObjectErrors() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError nestedError = new FieldError("customerDto", "address.street", "Street cannot be blank");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(nestedError));

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> details = response.getBody().getDetails();
        assertEquals("Street cannot be blank", details.get("address.street"));
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with special characters in messages")
    void handleConstraintViolationException_handlesSpecialCharactersInMessages() {
        // Given
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Value 'test@email' is not valid");
        when(path.toString()).thenReturn("email");
        
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException ex = new ConstraintViolationException("Special chars", violations);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> details = response.getBody().getDetails();
        assertEquals("Value 'test@email' is not valid", details.get("email"));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with empty message")
    void handleIllegalArgumentException_handlesEmptyMessage() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Business Logic Error", response.getBody().getMessage());
        
        Map<String, String> details = response.getBody().getDetails();
        assertEquals("", details.get("error"));
    }

    @Test
    @DisplayName("Should verify ErrorResponse structure in all handlers")
    void verifyErrorResponseStructure() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Test error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Then
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getTimestamp());
        assertNotNull(errorResponse.getMessage());
        assertNotNull(errorResponse.getDetails());
        assertTrue(errorResponse.getDetails() instanceof Map);
    }
}