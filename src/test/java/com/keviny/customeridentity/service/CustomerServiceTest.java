package com.keviny.customeridentity.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import com.keviny.customeridentity.model.CustomerIdentity;
import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.mapper.CustomerMapper;
import com.keviny.customeridentity.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;

class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create new customer when SSN not found")
    void createOrUpdate_createsNewWhenSsnNotFound() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("999-88-7777");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setGender("Male");
        dto.setDob(LocalDate.of(1990, 5, 15));

        CustomerIdentity savedEntity = new CustomerIdentity();
        savedEntity.setId(1L);
        savedEntity.setSsn(dto.getSsn());
        savedEntity.setFirstName(dto.getFirstName());
        savedEntity.setLastName(dto.getLastName());
        savedEntity.setGender(dto.getGender());
        savedEntity.setDob(dto.getDob());

        when(repository.save(any(CustomerIdentity.class))).thenReturn(savedEntity);

        // When
        CustomerIdentity result = service.createOrUpdateCustomer(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("Male", result.getGender());
        assertEquals(LocalDate.of(1990, 5, 15), result.getDob());
        verify(repository, times(1)).save(any(CustomerIdentity.class));
    }

    @Test
    @DisplayName("Should update existing customer when SSN constraint violation occurs")
    void createOrUpdate_updatesExistingWhenSsnFound() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("111-22-3333");
        dto.setFirstName("UpdatedFirst");
        dto.setLastName("UpdatedLast");
        dto.setGender("Female");
        dto.setDob(LocalDate.of(1985, 8, 20));

        CustomerIdentity existingEntity = new CustomerIdentity();
        existingEntity.setId(1L);
        existingEntity.setSsn("111-22-3333");
        existingEntity.setFirstName("OriginalFirst");
        existingEntity.setLastName("OriginalLast");
        existingEntity.setGender("Male");
        existingEntity.setDob(LocalDate.of(1980, 1, 1));

        // First save throws constraint violation, second save succeeds
        when(repository.save(any(CustomerIdentity.class)))
            .thenThrow(new DataIntegrityViolationException("ssn_unique"))
            .thenReturn(existingEntity);

        when(repository.findBySsn(dto.getSsn())).thenReturn(Optional.of(existingEntity));

        // When
        CustomerIdentity result = service.createOrUpdateCustomer(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("111-22-3333", result.getSsn());
        verify(repository, times(2)).save(any(CustomerIdentity.class));
        verify(repository, times(1)).findBySsn("111-22-3333");
    }

    @Test
    @DisplayName("Should find customer by ID when exists")
    void getCustomerById_returnsCustomerWhenExists() {
        // Given
        Long customerId = 1L;
        CustomerIdentity entity = new CustomerIdentity();
        entity.setId(customerId);
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setSsn("123-45-6789");

        when(repository.findById(customerId)).thenReturn(Optional.of(entity));

        // When
        Optional<CustomerIdentity> result = service.getCustomerById(customerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customerId, result.get().getId());
        assertEquals("John", result.get().getFirstName());
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Should return empty when customer ID does not exist")
    void getCustomerById_returnsEmptyWhenNotExists() {
        // Given
        Long customerId = 999L;
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // When
        Optional<CustomerIdentity> result = service.getCustomerById(customerId);

        // Then
        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Should handle null customer ID gracefully")
    void getCustomerById_handlesNullId() {
        // Given
        Long customerId = null;
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // When
        Optional<CustomerIdentity> result = service.getCustomerById(customerId);

        // Then
        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when constraint violation occurs but customer not found")
    void createOrUpdate_throwsIllegalStateWhenConstraintViolationButCustomerNotFound() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("orphan-ssn");
        dto.setFirstName("Test");
        dto.setLastName("User");

        when(repository.save(any(CustomerIdentity.class)))
            .thenThrow(new DataIntegrityViolationException("ssn_unique"));
        when(repository.findBySsn(dto.getSsn())).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> service.createOrUpdateCustomer(dto)
        );

        assertTrue(exception.getMessage().contains("Data integrity violation for SSN orphan-ssn"));
        verify(repository, times(1)).save(any(CustomerIdentity.class));
        verify(repository, times(1)).findBySsn("orphan-ssn");
    }

    @Test
    @DisplayName("Should create customer with minimal data")
    void createOrUpdate_createsCustomerWithMinimalData() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("minimal-ssn");
        dto.setFirstName("Min");
        dto.setLastName("User");

        CustomerIdentity savedEntity = new CustomerIdentity();
        savedEntity.setId(1L);
        savedEntity.setSsn(dto.getSsn());
        savedEntity.setFirstName(dto.getFirstName());
        savedEntity.setLastName(dto.getLastName());

        when(repository.save(any(CustomerIdentity.class))).thenReturn(savedEntity);

        // When
        CustomerIdentity result = service.createOrUpdateCustomer(dto);

        // Then
        assertNotNull(result);
        assertEquals("Min", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertNull(result.getGender());
        assertNull(result.getDob());
        verify(repository, times(1)).save(any(CustomerIdentity.class));
    }

    @Test
    @DisplayName("Should create customer with all fields populated")
    void createOrUpdate_createsCustomerWithAllFields() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("complete-ssn");
        dto.setFirstName("Complete");
        dto.setLastName("User");
        dto.setGender("Other");
        dto.setDob(LocalDate.of(1995, 12, 25));

        CustomerIdentity savedEntity = new CustomerIdentity();
        savedEntity.setId(2L);
        CustomerMapper.toEntity(dto, savedEntity);
        savedEntity.setId(2L); // Simulate DB assignment

        when(repository.save(any(CustomerIdentity.class))).thenReturn(savedEntity);

        // When
        CustomerIdentity result = service.createOrUpdateCustomer(dto);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Complete", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("Other", result.getGender());
        assertEquals(LocalDate.of(1995, 12, 25), result.getDob());
        verify(repository, times(1)).save(any(CustomerIdentity.class));
    }

    @Test
    @DisplayName("Should update existing customer with new gender")
    void createOrUpdate_updatesExistingCustomerGender() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("gender-update");
        dto.setFirstName("Gender");
        dto.setLastName("Update");
        dto.setGender("Female");

        CustomerIdentity existingEntity = new CustomerIdentity();
        existingEntity.setId(3L);
        existingEntity.setSsn("gender-update");
        existingEntity.setFirstName("Gender");
        existingEntity.setLastName("Update");
        existingEntity.setGender("Male");

        when(repository.save(any(CustomerIdentity.class)))
            .thenThrow(new DataIntegrityViolationException("ssn_unique"))
            .thenReturn(existingEntity);
        when(repository.findBySsn(dto.getSsn())).thenReturn(Optional.of(existingEntity));

        // When
        CustomerIdentity result = service.createOrUpdateCustomer(dto);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        verify(repository, times(2)).save(any(CustomerIdentity.class));
        verify(repository, times(1)).findBySsn("gender-update");
    }

    @Test
    @DisplayName("Should update existing customer with new date of birth")
    void createOrUpdate_updatesExistingCustomerDob() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("dob-update");
        dto.setFirstName("DOB");
        dto.setLastName("Update");
        dto.setDob(LocalDate.of(2000, 6, 15));

        CustomerIdentity existingEntity = new CustomerIdentity();
        existingEntity.setId(4L);
        existingEntity.setSsn("dob-update");
        existingEntity.setFirstName("DOB");
        existingEntity.setLastName("Update");
        existingEntity.setDob(LocalDate.of(1990, 1, 1));

        when(repository.save(any(CustomerIdentity.class)))
            .thenThrow(new DataIntegrityViolationException("ssn_unique"))
            .thenReturn(existingEntity);
        when(repository.findBySsn(dto.getSsn())).thenReturn(Optional.of(existingEntity));

        // When
        CustomerIdentity result = service.createOrUpdateCustomer(dto);

        // Then
        assertNotNull(result);
        assertEquals(4L, result.getId());
        verify(repository, times(2)).save(any(CustomerIdentity.class));
        verify(repository, times(1)).findBySsn("dob-update");
    }

    @Test
    @DisplayName("Should handle multiple sequential creates")
    void createOrUpdate_handlesMultipleSequentialCreates() {
        // Given
        CustomerDto dto1 = new CustomerDto();
        dto1.setSsn("seq-1");
        dto1.setFirstName("First");
        dto1.setLastName("Customer");

        CustomerDto dto2 = new CustomerDto();
        dto2.setSsn("seq-2");
        dto2.setFirstName("Second");
        dto2.setLastName("Customer");

        CustomerIdentity entity1 = new CustomerIdentity();
        entity1.setId(1L);
        CustomerMapper.toEntity(dto1, entity1);
        entity1.setId(1L);

        CustomerIdentity entity2 = new CustomerIdentity();
        entity2.setId(2L);
        CustomerMapper.toEntity(dto2, entity2);
        entity2.setId(2L);

        when(repository.save(any(CustomerIdentity.class)))
            .thenReturn(entity1)
            .thenReturn(entity2);

        // When
        CustomerIdentity result1 = service.createOrUpdateCustomer(dto1);
        CustomerIdentity result2 = service.createOrUpdateCustomer(dto2);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(1L, result1.getId());
        assertEquals(2L, result2.getId());
        verify(repository, times(2)).save(any(CustomerIdentity.class));
    }

    @Test
    @DisplayName("Should handle repository exception during update lookup")
    void createOrUpdate_handlesRepositoryExceptionDuringLookup() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("lookup-fail");
        dto.setFirstName("Lookup");
        dto.setLastName("Fail");

        when(repository.save(any(CustomerIdentity.class)))
            .thenThrow(new DataIntegrityViolationException("ssn_unique"));
        when(repository.findBySsn(dto.getSsn()))
            .thenThrow(new DataAccessException("Database connection lost") {});

        // When & Then
        DataAccessException exception = assertThrows(
            DataAccessException.class,
            () -> service.createOrUpdateCustomer(dto)
        );

        assertEquals("Database connection lost", exception.getMessage());
        verify(repository, times(1)).save(any(CustomerIdentity.class));
        verify(repository, times(1)).findBySsn("lookup-fail");
    }

    @Test
    @DisplayName("Should handle edge case with empty SSN during constraint violation")
    void createOrUpdate_handlesEmptySsnConstraintViolation() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("");
        dto.setFirstName("Empty");
        dto.setLastName("SSN");

        when(repository.save(any(CustomerIdentity.class)))
            .thenThrow(new DataIntegrityViolationException("ssn_unique"));
        when(repository.findBySsn("")).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> service.createOrUpdateCustomer(dto)
        );

        assertTrue(exception.getMessage().contains("Data integrity violation for SSN"));
        verify(repository, times(1)).save(any(CustomerIdentity.class));
        verify(repository, times(1)).findBySsn("");
    }

    @Test
    @DisplayName("Should handle successful update after constraint violation")
    void createOrUpdate_handlesSuccessfulUpdateAfterConstraintViolation() {
        // Given
        CustomerDto dto = new CustomerDto();
        dto.setSsn("success-update");
        dto.setFirstName("Success");
        dto.setLastName("Update");
        dto.setGender("Female");
        dto.setDob(LocalDate.of(1992, 3, 10));

        CustomerIdentity existingEntity = new CustomerIdentity();
        existingEntity.setId(5L);
        existingEntity.setSsn("success-update");
        existingEntity.setFirstName("Old");
        existingEntity.setLastName("Name");

        CustomerIdentity updatedEntity = new CustomerIdentity();
        updatedEntity.setId(5L);
        updatedEntity.setSsn("success-update");
        updatedEntity.setFirstName("Success");
        updatedEntity.setLastName("Update");
        updatedEntity.setGender("Female");
        updatedEntity.setDob(LocalDate.of(1992, 3, 10));

        when(repository.save(any(CustomerIdentity.class)))
            .thenThrow(new DataIntegrityViolationException("ssn_unique"))
            .thenReturn(updatedEntity);
        when(repository.findBySsn(dto.getSsn())).thenReturn(Optional.of(existingEntity));

        // When
        CustomerIdentity result = service.createOrUpdateCustomer(dto);

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Success", result.getFirstName());
        assertEquals("Update", result.getLastName());
        assertEquals("Female", result.getGender());
        assertEquals(LocalDate.of(1992, 3, 10), result.getDob());
        verify(repository, times(2)).save(any(CustomerIdentity.class));
        verify(repository, times(1)).findBySsn("success-update");
    }

    @Test
    @DisplayName("Should verify service constructor initializes repository dependency")
    void constructor_initializesRepositoryDependency() {
        // Given
        CustomerRepository mockRepo = mock(CustomerRepository.class);

        // When
        CustomerService testService = new CustomerService(mockRepo);

        // Then
        assertNotNull(testService);
        // The service should work with the injected repository
        when(mockRepo.findById(1L)).thenReturn(Optional.empty());
        Optional<CustomerIdentity> result = testService.getCustomerById(1L);
        assertFalse(result.isPresent());
        verify(mockRepo, times(1)).findById(1L);
    }
}
