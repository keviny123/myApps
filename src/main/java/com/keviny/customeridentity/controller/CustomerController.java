package com.keviny.customeridentity.controller;

import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.mapper.CustomerMapper;
import com.keviny.customeridentity.model.CustomerIdentity;
import com.keviny.customeridentity.service.CustomerService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {    
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getById(@PathVariable @Min(value = 1, message = "Customer ID must be positive") Long id) {
        return customerService.getCustomerById(id)
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
