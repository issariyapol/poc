package com.issa.poc.repository;

import com.issa.poc.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCid(String cid);
    Optional<Customer> findByEmail(String email);
}
