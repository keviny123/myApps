package com.keviny.customeridentity.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerIdentityTest {

    @Test
    @DisplayName("Should create entity with default constructor")
    void shouldCreateEntityWithDefaultConstructor() {
        // When
        CustomerIdentity entity = new CustomerIdentity();

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getFirstName());
        assertNull(entity.getLastName());
        assertNull(entity.getGender());
        assertNull(entity.getDob());
        assertNull(entity.getSsn());
    }

    @Test
    @DisplayName("Should set and get ID")
    void shouldSetAndGetId() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        Long id = 123L;

        // When
        entity.setId(id);

        // Then
        assertEquals(id, entity.getId());
    }

    @Test
    @DisplayName("Should set and get first name")
    void shouldSetAndGetFirstName() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        String firstName = "John";

        // When
        entity.setFirstName(firstName);

        // Then
        assertEquals(firstName, entity.getFirstName());
    }

    @Test
    @DisplayName("Should set and get last name")
    void shouldSetAndGetLastName() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        String lastName = "Doe";

        // When
        entity.setLastName(lastName);

        // Then
        assertEquals(lastName, entity.getLastName());
    }

    @Test
    @DisplayName("Should set and get gender")
    void shouldSetAndGetGender() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        String gender = "Male";

        // When
        entity.setGender(gender);

        // Then
        assertEquals(gender, entity.getGender());
    }

    @Test
    @DisplayName("Should set and get date of birth")
    void shouldSetAndGetDob() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        LocalDate dob = LocalDate.of(1990, 5, 15);

        // When
        entity.setDob(dob);

        // Then
        assertEquals(dob, entity.getDob());
    }

    @Test
    @DisplayName("Should set and get SSN")
    void shouldSetAndGetSsn() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        String ssn = "123-45-6789";

        // When
        entity.setSsn(ssn);

        // Then
        assertEquals(ssn, entity.getSsn());
    }

    @Test
    @DisplayName("Should handle null values for all properties")
    void shouldHandleNullValuesForAllProperties() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();

        // When
        entity.setId(null);
        entity.setFirstName(null);
        entity.setLastName(null);
        entity.setGender(null);
        entity.setDob(null);
        entity.setSsn(null);

        // Then
        assertNull(entity.getId());
        assertNull(entity.getFirstName());
        assertNull(entity.getLastName());
        assertNull(entity.getGender());
        assertNull(entity.getDob());
        assertNull(entity.getSsn());
    }

    @Test
    @DisplayName("Should handle all possible gender values")
    void shouldHandleAllPossibleGenderValues() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();

        // Test Male
        entity.setGender("Male");
        assertEquals("Male", entity.getGender());

        // Test Female
        entity.setGender("Female");
        assertEquals("Female", entity.getGender());

        // Test Other
        entity.setGender("Other");
        assertEquals("Other", entity.getGender());

        // Test custom value
        entity.setGender("Non-binary");
        assertEquals("Non-binary", entity.getGender());
    }

    @Test
    @DisplayName("Should handle maximum length strings")
    void shouldHandleMaximumLengthStrings() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        String maxFirstName = "a".repeat(100);
        String maxLastName = "b".repeat(100);
        String maxGender = "c".repeat(32);
        String maxSsn = "d".repeat(32);

        // When
        entity.setFirstName(maxFirstName);
        entity.setLastName(maxLastName);
        entity.setGender(maxGender);
        entity.setSsn(maxSsn);

        // Then
        assertEquals(maxFirstName, entity.getFirstName());
        assertEquals(maxLastName, entity.getLastName());
        assertEquals(maxGender, entity.getGender());
        assertEquals(maxSsn, entity.getSsn());
        assertEquals(100, entity.getFirstName().length());
        assertEquals(100, entity.getLastName().length());
        assertEquals(32, entity.getGender().length());
        assertEquals(32, entity.getSsn().length());
    }

    @Test
    @DisplayName("Should handle date edge cases")
    void shouldHandleDateEdgeCases() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();

        // Test minimum date
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        entity.setDob(minDate);
        assertEquals(minDate, entity.getDob());

        // Test maximum past date (yesterday)
        LocalDate yesterday = LocalDate.now().minusDays(1);
        entity.setDob(yesterday);
        assertEquals(yesterday, entity.getDob());

        // Test leap year date
        LocalDate leapYear = LocalDate.of(2000, 2, 29);
        entity.setDob(leapYear);
        assertEquals(leapYear, entity.getDob());
    }

    @Test
    @DisplayName("Should create entity with all fields populated")
    void shouldCreateEntityWithAllFieldsPopulated() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        Long id = 1L;
        String firstName = "Alice";
        String lastName = "Johnson";
        String gender = "Female";
        LocalDate dob = LocalDate.of(1985, 8, 20);
        String ssn = "987-65-4321";

        // When
        entity.setId(id);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setGender(gender);
        entity.setDob(dob);
        entity.setSsn(ssn);

        // Then
        assertEquals(id, entity.getId());
        assertEquals(firstName, entity.getFirstName());
        assertEquals(lastName, entity.getLastName());
        assertEquals(gender, entity.getGender());
        assertEquals(dob, entity.getDob());
        assertEquals(ssn, entity.getSsn());
    }
}