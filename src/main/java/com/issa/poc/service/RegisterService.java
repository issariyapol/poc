package com.issa.poc.service;

import com.issa.poc.exception.BadRequestRegisterException;
import com.issa.poc.model.Customer;
import com.issa.poc.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    public void newCustomer(Customer customer) throws Exception {
        if(customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new BadRequestRegisterException("Already Register by this Email");
        }

        if(customerRepository.findByCid(customer.getCid()).isPresent()) {
            throw new BadRequestRegisterException("Already Register by this citizen ID");
        }

        if(customer.getCid().isEmpty()){
            throw new BadRequestRegisterException("Citizen Id is required");
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setStatus("ACTIVE");
        customer.setCreateDate(LocalDateTime.now());
        customer.setLastUpdateDate(LocalDateTime.now());
        customerRepository.save(customer);
    }
}
