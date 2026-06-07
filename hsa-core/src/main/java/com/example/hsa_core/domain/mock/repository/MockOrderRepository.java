package com.example.hsa_core.domain.mock.repository;

import com.example.hsa_core.domain.customer.Customer;
import com.example.hsa_core.domain.mock.MockOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockOrderRepository extends JpaRepository<MockOrder, Long> {

    List<MockOrder> findByCustomerOrderByOrderedAtDesc(Customer customer);
}
