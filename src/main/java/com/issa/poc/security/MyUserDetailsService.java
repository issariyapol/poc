package com.issa.poc.security;

import com.issa.poc.model.Customer;
import com.issa.poc.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByEmail(username);
        if(customer.isEmpty()){
            throw new UsernameNotFoundException("Invalid Username/Password");
        }
        return new User(customer.get().getEmail(),customer.get().getPassword(), new ArrayList<>());
    }

    public Customer getCustomerFromAuthHeader(String authHeader){
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Optional<Customer> customer = customerRepository.findByEmail(email);
        return customer.orElse(null);
    }
}

