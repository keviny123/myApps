package com.keviny.customeridentity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.model.CustomerIdentity;
import com.keviny.customeridentity.service.CustomerService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    void getById_returnsCustomer() throws Exception {
        CustomerIdentity entity = new CustomerIdentity();
        entity.setId(1L);
        entity.setFirstName("Jane");
        entity.setLastName("Doe");
        entity.setSsn("111-22-3333");

        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(entity));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void post_upsert_returnsCreated() throws Exception {
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setDob(LocalDate.of(1985, 5, 1));
        dto.setSsn("222-33-4444");

        CustomerIdentity saved = new CustomerIdentity();
        saved.setId(42L);
        saved.setFirstName(dto.getFirstName());
        saved.setLastName(dto.getLastName());
        saved.setSsn(dto.getSsn());

        when(customerService.createOrUpdateCustomer(any(CustomerDto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/customers")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/customers/42"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void post_withInvalidDto_returnsBadRequest() throws Exception {
        CustomerDto dto = new CustomerDto();
        dto.setFirstName(""); // Invalid: first name is blank
        dto.setSsn(null); // Invalid: ssn is null
        dto.setDob(LocalDate.now().plusDays(1)); // Invalid: dob is in the future

        mockMvc.perform(post("/api/customers")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.details.firstName").value("First name cannot be blank"))
                .andExpect(jsonPath("$.details.lastName").value("Last name cannot be blank")) // Also invalid as it was never set
                .andExpect(jsonPath("$.details.ssn").value("SSN cannot be blank"))
                .andExpect(jsonPath("$.details.dob").value("Date of birth must be in the past"));

        verifyNoInteractions(customerService);
    }
}
