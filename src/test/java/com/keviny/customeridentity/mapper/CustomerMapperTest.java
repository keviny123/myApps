package com.keviny.customeridentity.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.model.CustomerIdentity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

    @Test
    @DisplayName("Should convert entity to DTO with all fields")
    void toDto_convertsEntityToDtoWithAllFields() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        entity.setId(1L);
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setGender("Male");
        entity.setDob(LocalDate.of(1990, 5, 15));
        entity.setSsn("123-45-6789");

        // When
        CustomerDto dto = CustomerMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("Male", dto.getGender());
        assertEquals(LocalDate.of(1990, 5, 15), dto.getDob());
        assertEquals("123-45-6789", dto.getSsn());
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void toDto_returnsNullWhenEntityIsNull() {
        // When
        CustomerDto dto = CustomerMapper.toDto(null);

        // Then
        assertNull(dto);
    }

    @Test
    @DisplayName("Should convert entity to DTO with minimal fields")
    void toDto_convertsEntityToDtoWithMinimalFields() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        entity.setId(2L);
        entity.setFirstName("Jane");
        entity.setLastName("Smith");
        entity.setSsn("987-65-4321");
        // gender and dob are null

        // When
        CustomerDto dto = CustomerMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertNull(dto.getGender());
        assertNull(dto.getDob());
        assertEquals("987-65-4321", dto.getSsn());
    }

    @Test
    @DisplayName("Should map DTO to new entity with SSN")
    void toEntity_mapsDtoToNewEntityWithSsn() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setId(999L); // This should be ignored for new entities
        dto.setFirstName("Alice");
        dto.setLastName("Johnson");
        dto.setGender("Female");
        dto.setDob(LocalDate.of(1985, 8, 20));
        dto.setSsn("111-22-3333");

        CustomerIdentity entity = new CustomerIdentity(); // New entity (no ID)

        // When
        CustomerMapper.toEntity(dto, entity);

        // Then
        assertNull(entity.getId()); // ID should not be set from DTO
        assertEquals("Alice", entity.getFirstName());
        assertEquals("Johnson", entity.getLastName());
        assertEquals("Female", entity.getGender());
        assertEquals(LocalDate.of(1985, 8, 20), entity.getDob());
        assertEquals("111-22-3333", entity.getSsn()); // SSN should be set for new entity
    }

    @Test
    @DisplayName("Should map DTO to existing entity without changing SSN")
    void toEntity_mapsDtoToExistingEntityWithoutChangingSsn() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setId(999L); // This should be ignored
        dto.setFirstName("Bob");
        dto.setLastName("Wilson");
        dto.setGender("Male");
        dto.setDob(LocalDate.of(1992, 12, 10));
        dto.setSsn("new-ssn"); // This should be ignored for existing entity

        CustomerIdentity entity = new CustomerIdentity();
        entity.setId(5L); // Existing entity (has ID)
        entity.setSsn("original-ssn");
        entity.setFirstName("Original");
        entity.setLastName("Name");

        // When
        CustomerMapper.toEntity(dto, entity);

        // Then
        assertEquals(5L, entity.getId()); // ID should remain unchanged
        assertEquals("Bob", entity.getFirstName());
        assertEquals("Wilson", entity.getLastName());
        assertEquals("Male", entity.getGender());
        assertEquals(LocalDate.of(1992, 12, 10), entity.getDob());
        assertEquals("original-ssn", entity.getSsn()); // SSN should remain unchanged for existing entity
    }

    @Test
    @DisplayName("Should handle null DTO gracefully")
    void toEntity_handlesNullDtoGracefully() {
        // Given
        CustomerIdentity entity = new CustomerIdentity();
        entity.setId(1L);
        entity.setFirstName("Original");

        // When
        CustomerMapper.toEntity(null, entity);

        // Then
        // Entity should remain unchanged
        assertEquals(1L, entity.getId());
        assertEquals("Original", entity.getFirstName());
    }

    @Test
    @DisplayName("Should handle null entity gracefully")
    void toEntity_handlesNullEntityGracefully() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setSsn("test-ssn");

        // When & Then
        // Should not throw exception
        assertDoesNotThrow(() -> CustomerMapper.toEntity(dto, null));
    }

    @Test
    @DisplayName("Should map partial DTO data to entity")
    void toEntity_mapsPartialDtoDataToEntity() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Partial");
        dto.setLastName("Data");
        dto.setSsn("partial-ssn");
        // gender and dob are null

        CustomerIdentity entity = new CustomerIdentity(); // New entity

        // When
        CustomerMapper.toEntity(dto, entity);

        // Then
        assertEquals("Partial", entity.getFirstName());
        assertEquals("Data", entity.getLastName());
        assertNull(entity.getGender());
        assertNull(entity.getDob());
        assertEquals("partial-ssn", entity.getSsn());
    }
}