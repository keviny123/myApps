package com.keviny.customeridentity.controller;

import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.mapper.CustomerMapper;
import com.keviny.customeridentity.model.CustomerIdentity;
import com.keviny.customeridentity.service.CustomerService;
import com.keviny.customeridentity.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    public CustomerController(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getById(@PathVariable Long id) { // No changes needed here, but showing for context
        return customerRepository.findById(id)
            .map(CustomerMapper::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createOrUpdate(@Valid @RequestBody CustomerDto dto) {
        CustomerIdentity savedEntity = customerService.createOrUpdateCustomer(dto);

        URI location = URI.create("/api/customers/" + savedEntity.getId());
        CustomerDto resultDto = CustomerMapper.toDto(savedEntity);
        return ResponseEntity.created(location).body(resultDto);
    }
}
