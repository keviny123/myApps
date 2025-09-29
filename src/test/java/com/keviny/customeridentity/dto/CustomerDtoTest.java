package com.keviny.customeridentity.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CustomerDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid DTO with all fields")
    void createValidDtoWithAllFields() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setGender("Male");
        dto.setDob(LocalDate.of(1990, 5, 15));
        dto.setSsn("123-45-6789");

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("Male", dto.getGender());
        assertEquals(LocalDate.of(1990, 5, 15), dto.getDob());
        assertEquals("123-45-6789", dto.getSsn());
    }

    @Test
    @DisplayName("Should fail validation when firstName is blank")
    void shouldFailValidationWhenFirstNameIsBlank() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("");
        dto.setLastName("Doe");
        dto.setSsn("123-45-6789");
        dto.setDob(LocalDate.of(1990, 5, 15));

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("firstName", violation.getPropertyPath().toString());
        assertEquals("First name cannot be blank", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when firstName is null")
    void shouldFailValidationWhenFirstNameIsNull() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName(null);
        dto.setLastName("Doe");
        dto.setSsn("123-45-6789");
        dto.setDob(LocalDate.of(1990, 5, 15));

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("firstName", violation.getPropertyPath().toString());
        assertEquals("First name cannot be blank", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when lastName is blank")
    void shouldFailValidationWhenLastNameIsBlank() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("");
        dto.setSsn("123-45-6789");
        dto.setDob(LocalDate.of(1990, 5, 15));

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("lastName", violation.getPropertyPath().toString());
        assertEquals("Last name cannot be blank", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when SSN is blank")
    void shouldFailValidationWhenSsnIsBlank() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSsn("");
        dto.setDob(LocalDate.of(1990, 5, 15));

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("ssn", violation.getPropertyPath().toString());
        assertEquals("SSN cannot be blank", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when SSN is null")
    void shouldFailValidationWhenSsnIsNull() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSsn(null);
        dto.setDob(LocalDate.of(1990, 5, 15));

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("ssn", violation.getPropertyPath().toString());
        assertEquals("SSN cannot be blank", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when date of birth is in the future")
    void shouldFailValidationWhenDobIsInFuture() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSsn("123-45-6789");
        dto.setDob(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("dob", violation.getPropertyPath().toString());
        assertEquals("Date of birth must be in the past", violation.getMessage());
    }

    @Test
    @DisplayName("Should pass validation when date of birth is null")
    void shouldPassValidationWhenDobIsNull() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSsn("123-45-6789");
        dto.setDob(null);

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when firstName exceeds 100 characters")
    void shouldFailValidationWhenFirstNameTooLong() {
        // Given
        String longName = "a".repeat(101);
        CustomerDto dto = new CustomerDto();
        dto.setFirstName(longName);
        dto.setLastName("Doe");
        dto.setSsn("123-45-6789");

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("firstName", violation.getPropertyPath().toString());
        assertEquals("First name cannot be longer than 100 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when lastName exceeds 100 characters")
    void shouldFailValidationWhenLastNameTooLong() {
        // Given
        String longName = "b".repeat(101);
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName(longName);
        dto.setSsn("123-45-6789");

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("lastName", violation.getPropertyPath().toString());
        assertEquals("Last name cannot be longer than 100 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when SSN exceeds 32 characters")
    void shouldFailValidationWhenSsnTooLong() {
        // Given
        String longSsn = "1".repeat(33);
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSsn(longSsn);

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("ssn", violation.getPropertyPath().toString());
        assertEquals("SSN cannot be longer than 32 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when gender is invalid")
    void shouldFailValidationWhenGenderIsInvalid() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSsn("123-45-6789");
        dto.setGender("Invalid");

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<CustomerDto> violation = violations.iterator().next();
        assertEquals("gender", violation.getPropertyPath().toString());
        assertEquals("Gender must be 'Male', 'Female', or 'Other'", violation.getMessage());
    }

    @Test
    @DisplayName("Should pass validation with valid gender values")
    void shouldPassValidationWithValidGenderValues() {
        // Test Male
        CustomerDto dto1 = new CustomerDto();
        dto1.setFirstName("John");
        dto1.setLastName("Doe");
        dto1.setSsn("123-45-6789");
        dto1.setGender("Male");
        assertTrue(validator.validate(dto1).isEmpty());

        // Test Female
        CustomerDto dto2 = new CustomerDto();
        dto2.setFirstName("Jane");
        dto2.setLastName("Doe");
        dto2.setSsn("987-65-4321");
        dto2.setGender("Female");
        assertTrue(validator.validate(dto2).isEmpty());

        // Test Other
        CustomerDto dto3 = new CustomerDto();
        dto3.setFirstName("Alex");
        dto3.setLastName("Doe");
        dto3.setSsn("555-55-5555");
        dto3.setGender("Other");
        assertTrue(validator.validate(dto3).isEmpty());
    }

    @Test
    @DisplayName("Should pass validation with case insensitive gender values")
    void shouldPassValidationWithCaseInsensitiveGenderValues() {
        // Test different cases
        CustomerDto dto1 = new CustomerDto();
        dto1.setFirstName("John");
        dto1.setLastName("Doe");
        dto1.setSsn("123-45-6789");
        dto1.setGender("male");
        assertTrue(validator.validate(dto1).isEmpty());

        CustomerDto dto2 = new CustomerDto();
        dto2.setFirstName("Jane");
        dto2.setLastName("Doe");
        dto2.setSsn("987-65-4321");
        dto2.setGender("FEMALE");
        assertTrue(validator.validate(dto2).isEmpty());

        CustomerDto dto3 = new CustomerDto();
        dto3.setFirstName("Alex");
        dto3.setLastName("Doe");
        dto3.setSsn("555-55-5555");
        dto3.setGender("oThEr");
        assertTrue(validator.validate(dto3).isEmpty());
    }

    @Test
    @DisplayName("Should pass validation when gender is null")
    void shouldPassValidationWhenGenderIsNull() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSsn("123-45-6789");
        dto.setGender(null);

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple validation errors")
    void shouldHandleMultipleValidationErrors() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName(""); // Invalid: blank
        dto.setLastName(""); // Invalid: blank
        dto.setSsn(""); // Invalid: blank
        dto.setDob(LocalDate.now().plusDays(1)); // Invalid: future date

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // Then
        assertEquals(4, violations.size());
    }

    @Test
    @DisplayName("Should test getter and setter methods")
    void shouldTestGetterAndSetterMethods() {
        // Given
        CustomerDto dto = new CustomerDto();
        Long id = 123L;
        String firstName = "Test";
        String lastName = "User";
        String gender = "Male";
        LocalDate dob = LocalDate.of(1990, 1, 1);
        String ssn = "123-45-6789";

        // When
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setGender(gender);
        dto.setDob(dob);
        dto.setSsn(ssn);

        // Then
        assertEquals(id, dto.getId());
        assertEquals(firstName, dto.getFirstName());
        assertEquals(lastName, dto.getLastName());
        assertEquals(gender, dto.getGender());
        assertEquals(dob, dto.getDob());
        assertEquals(ssn, dto.getSsn());
    }
}