package com.keviny.customeridentity.mapper;

import com.keviny.customeridentity.dto.CustomerDto;
import com.keviny.customeridentity.model.CustomerIdentity;

public class CustomerMapper {

    public static CustomerDto toDto(CustomerIdentity entity) {
        if (entity == null) {
            return null;
        }

        CustomerDto dto = new CustomerDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setGender(entity.getGender());
        dto.setDob(entity.getDob());
        dto.setSsn(entity.getSsn());

        return dto;
    }

    public static void toEntity(CustomerDto dto, CustomerIdentity entity) {
        if (dto == null || entity == null) {
            return;
        }

        // We don't map the ID from DTO to entity to prevent changing the primary key of an existing entity.
        // The SSN is also typically immutable, so we only set it if the entity is new (has no ID).
        if (entity.getId() == null) { // This block is for new entities only
            entity.setSsn(dto.getSsn());
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setGender(dto.getGender());
        entity.setDob(dto.getDob());
    }
}