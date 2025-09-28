package com.keviny.customeridentity.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.keviny.customeridentity.model.CustomerIdentity;
import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    void createOrUpdate_createsNewWhenSsnNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setSsn("999-88-7777");
        dto.setFirstName("Test");
        dto.setLastName("User");

        when(repository.findBySsn(dto.getSsn())).thenReturn(Optional.empty());
        when(repository.save(any(CustomerIdentity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerIdentity saved = service.createOrUpdateCustomer(dto);

        assertNotNull(saved);
        assertEquals("Test", saved.getFirstName());
        verify(repository).save(any(CustomerIdentity.class));
    }
}
