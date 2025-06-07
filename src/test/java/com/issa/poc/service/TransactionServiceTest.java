package com.issa.poc.service;

import com.issa.poc.exception.AccountNotFoundException;
import com.issa.poc.exception.DepositAmountLessThanOneException;
import com.issa.poc.exception.TransInsufficientFundException;
import com.issa.poc.model.Account;
import com.issa.poc.model.Customer;
import com.issa.poc.model.Transaction;
import com.issa.poc.repository.AccountRepository;
import com.issa.poc.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransferSuccess()  {
        Customer sender = new Customer();
        sender.setId(1L);
        sender.setCid("00000");

        Account senderAccount = new Account();
        senderAccount.setBalance(1000.0);
        senderAccount.setCustomer(sender);
        senderAccount.setAccountNumber("0000AC1");

        Customer receiver = new Customer();
        receiver.setId(2L);
        receiver.setCid("11111");

        Account receiverAccount = new Account();
        receiverAccount.setBalance(1000.0);
        receiverAccount.setCustomer(receiver);
        receiverAccount.setAccountNumber("0000AC2");

        when(accountRepository.findByAccountNumber("0000AC1")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("0000AC2")).thenReturn(Optional.of(receiverAccount));

        try {
            Account returnAccount = transactionService.transfer(senderAccount.getAccountNumber(),"0000AC2",1000.00,"1001", "teller1");
            assertEquals(0, senderAccount.getBalance());
            assertEquals(2000, receiverAccount.getBalance());
            assertEquals(returnAccount, senderAccount);

            verify(transactionRepository, times(4)).save(any(Transaction.class));
        } catch (AccountNotFoundException | TransInsufficientFundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDepositSuccess() throws AccountNotFoundException, DepositAmountLessThanOneException {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCid("00000");

        Account account = new Account();
        account.setBalance(1000.0);
        account.setCustomer(customer);
        account.setAccountNumber("0000AC1");

        when(accountRepository.findByAccountNumber("0000AC1")).thenReturn(Optional.of(account));

        transactionService.deposit(account.getAccountNumber(),1000.0,"1001","tellerId");

        assertEquals(2000.00, account.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawSuccess() throws Exception, TransInsufficientFundException, AccountNotFoundException {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCid("00000");

        Account account = new Account();
        account.setBalance(1000.0);
        account.setCustomer(customer);
        account.setAccountNumber("0000AC1");

        when(accountRepository.findByAccountNumber("0000AC1")).thenReturn(Optional.of(account));

        transactionService.withdraw(account.getAccountNumber(),1000.0,"1001","tellerId");
        assertEquals(0.00, account.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawErrorInsufficientFund(){
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCid("00000");

        Account account = new Account();
        account.setBalance(0.00);
        account.setCustomer(customer);
        account.setAccountNumber("0000AC1");

        when(accountRepository.findByAccountNumber("0000AC1")).thenReturn(Optional.of(account));

        TransInsufficientFundException exception =assertThrows(TransInsufficientFundException.class, ()-> transactionService.withdraw(account.getAccountNumber(),1000.0,"1001","tellerId"));
    }
}
