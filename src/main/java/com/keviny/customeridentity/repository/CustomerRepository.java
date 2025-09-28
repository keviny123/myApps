
package com.keviny.customeridentity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keviny.customeridentity.model.CustomerIdentity;

public interface CustomerRepository extends JpaRepository<CustomerIdentity, Long> {

    Optional<CustomerIdentity> findBySsn(String ssn);
}