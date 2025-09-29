package com.keviny.customeridentity.service;

import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
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

    public Optional<CustomerIdentity> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public CustomerIdentity createOrUpdateCustomer(CustomerDto customerDto) {
        try {
            // Attempt to save a new customer. This is optimistic and avoids a SELECT query for new entries.
            CustomerIdentity newCustomer = new CustomerIdentity();
            CustomerMapper.toEntity(customerDto, newCustomer);
            return customerRepository.save(newCustomer);
        } catch (DataIntegrityViolationException e) {
            // If save fails due to a unique constraint (e.g., SSN exists), it's an update.
            // We find the existing customer and update their details.
            CustomerIdentity customer = customerRepository.findBySsn(customerDto.getSsn())
                    .orElseThrow(() -> new IllegalStateException(
                        "Data integrity violation for SSN " + customerDto.getSsn() + " but customer not found.", e));
            
            // Map updated properties from DTO to the existing entity
            CustomerMapper.toEntity(customerDto, customer);
            
            // Save the updated entity
            return customerRepository.save(customer);
        }
    }
}
