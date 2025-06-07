package com.issa.poc.service;

import com.issa.poc.exception.BadRequestRegisterException;
import com.issa.poc.model.Customer;
import com.issa.poc.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    @InjectMocks
    private RegisterService registerService;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        Customer customer = new Customer();
        customer.setCid("00000");
        customer.setThaiName("ThaiName");
        customer.setEngName("EngName");
        customer.setPin("123456");
        customer.setPassword("password");

        registerService.newCustomer(customer);
    }

    @Test
    void testRegisterErrorEmailAlreadyUsed() {
        Customer customer = new Customer();
        customer.setCid("00000");
        customer.setThaiName("ThaiName");
        customer.setEngName("EngName");
        customer.setPin("123456");
        customer.setEmail("Email");
        customer.setPassword("password");

        when(customerRepository.findByEmail("Email")).thenReturn(Optional.of(customer));

        BadRequestRegisterException exception = assertThrows(BadRequestRegisterException.class, ()
                -> registerService.newCustomer(customer));

    }

}
