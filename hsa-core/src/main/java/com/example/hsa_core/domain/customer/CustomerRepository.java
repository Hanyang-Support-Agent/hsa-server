package com.example.hsa_core.domain.customer;

import com.example.hsa_core.domain.channel.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdentifierAndChannel_Type(String identifier, ChannelType channelType);

}
