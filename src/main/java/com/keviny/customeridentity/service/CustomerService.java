package com.keviny.customeridentity.service;

import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.mapper.CustomerMapper;
import com.keviny.customeridentity.model.CustomerIdentity;
import com.keviny.customeridentity.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<CustomerIdentity> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public CustomerIdentity createOrUpdateCustomer(CustomerDto customerDto) {
        // Validate required fields
        validateCustomerDto(customerDto);
        
        // Find an existing customer by the unique SSN, or create a new one if not found.
        CustomerIdentity customer = customerRepository.findBySsn(customerDto.getSsn())
                .orElseGet(CustomerIdentity::new);
        
        // Map properties from DTO to the entity
        CustomerMapper.toEntity(customerDto, customer);
        
        try {
            return customerRepository.save(customer);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Customer with SSN '" + customerDto.getSsn() + "' already exists", e);
        }
    }
    
    private void validateCustomerDto(CustomerDto customerDto) {
        if (customerDto == null) {
            throw new IllegalArgumentException("Customer data cannot be null");
        }
        
        if (!StringUtils.hasText(customerDto.getSsn())) {
            throw new IllegalArgumentException("SSN cannot be null or empty");
        }
        
        if (!StringUtils.hasText(customerDto.getFirstName())) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        
        if (!StringUtils.hasText(customerDto.getLastName())) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        
        // Validate gender if provided
        if (StringUtils.hasText(customerDto.getGender())) {
            String gender = customerDto.getGender().toLowerCase();
            if (!gender.equals("male") && !gender.equals("female") && !gender.equals("other")) {
                throw new IllegalArgumentException("Gender must be 'Male', 'Female', or 'Other'");
            }
        }
        
        // Validate SSN format (basic check for 9-11 characters)
        if (customerDto.getSsn().length() < 9 || customerDto.getSsn().length() > 11) {
            throw new IllegalArgumentException("SSN must be between 9 and 11 characters");
        }
    }
}
