package com.example.hsa_core.domain.mock.repository;

import com.example.hsa_core.domain.customer.Customer;
import com.example.hsa_core.domain.mock.MockDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MockDeliberyRepository extends JpaRepository<MockDelivery, Long> {

    Optional<MockDelivery> findByCustomer(Customer customer);
}
