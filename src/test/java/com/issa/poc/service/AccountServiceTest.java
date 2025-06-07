package com.issa.poc.service;

import com.issa.poc.exception.AccountInvalidCustomerNameException;
import com.issa.poc.exception.CustomerNotFoundException;
import com.issa.poc.exception.DepositAmountLessThanOneException;
import com.issa.poc.model.Account;
import com.issa.poc.repository.AccountRepository;
import com.issa.poc.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionService transactionService;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNewAccountSuccess() throws AccountInvalidCustomerNameException, CustomerNotFoundException {
        Account account = accountService.addNewAccount("cid", "thaiName", "engName", "tellerId");

        assertEquals(0.0, account.getBalance());
        assertNotNull(account.getAccountNumber());
        assertEquals("cid", account.getCustomer().getCid());
        assertEquals(account.getAccountNumber(), accountService.getAccountNumberFromId(account.getId()));
    }

    @Test
    void testNewAccountWithDepositSuccess() throws AccountInvalidCustomerNameException, CustomerNotFoundException, DepositAmountLessThanOneException {
        Account account = accountService.addNewAccountWithDeposit("cid", "thaiName", "engName", 1000.00,"tellerId","1001");
    }

}
