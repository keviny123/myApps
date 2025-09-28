package com.keviny.customeridentity.service; // Note: This file seems to be in the wrong package. It should be in `com.keviny.customeridentity.service`

import org.springframework.stereotype.Service;

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

    public CustomerIdentity createOrUpdateCustomer(CustomerDto customerDto) {
        // Find an existing customer by the unique SSN, or create a new one if not found.
        CustomerIdentity customer = customerRepository.findBySsn(customerDto.getSsn())
                .orElseGet(CustomerIdentity::new);
        
        // Map properties from DTO to the entity
        CustomerMapper.toEntity(customerDto, customer);
        return customerRepository.save(customer);
    }
}