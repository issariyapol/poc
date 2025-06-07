package com.issa.poc.service;

import com.issa.poc.exception.AccountNotFoundException;
import com.issa.poc.exception.DepositAmountLessThanOneException;
import com.issa.poc.exception.TransInsufficientFundException;
import com.issa.poc.model.Account;
import com.issa.poc.model.Transaction;
import com.issa.poc.repository.AccountRepository;
import com.issa.poc.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Account deposit(String accountNumber, Double depositAmount, String terminalId, String tellerId) throws AccountNotFoundException, DepositAmountLessThanOneException {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isPresent()) {
            return deposit(account.get(), depositAmount, terminalId, tellerId);
        } else {
            throw new AccountNotFoundException();
        }
    }

    @Transactional
    public Account deposit(Account account, Double depositAmount, String terminalId, String tellerId) throws DepositAmountLessThanOneException {
        if(depositAmount < 1) {
            throw new DepositAmountLessThanOneException();
        }

        double currentBalance = account.getBalance();
        double newBalance = currentBalance + depositAmount;

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTerminalId(terminalId);
        transaction.setCode("A0");
        transaction.setChannel("OTC");
        transaction.setType("DEPOSIT");
        transaction.setSide("DR");
        transaction.setAmount(depositAmount);
        transaction.setBalance(newBalance);
        transaction.setTransactionBy(tellerId);
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setRemark("Deposit Terminal " + terminalId);

        transactionRepository.save(transaction);
        account.setBalance(newBalance);
        accountRepository.save(account);
        return account;
    }

    public Account withdraw(String accountNumber, Double amount, String terminalId, String tellerId) throws Exception, AccountNotFoundException, TransInsufficientFundException {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

        if (account.isEmpty()) {
            throw new AccountNotFoundException();
        }

        return withdraw(account.get(), amount, terminalId, tellerId);
    }

    @Transactional
    public Account withdraw(Account account, Double amount, String terminalId, String tellerId) throws Exception, TransInsufficientFundException {
        double currentBalance = account.getBalance();

        if (currentBalance < amount) {
            throw new TransInsufficientFundException();
        }

        double newBalance = currentBalance - amount;

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTerminalId(terminalId);
        transaction.setCode("A3");
        transaction.setType("WITHDRAW");
        transaction.setChannel("OTC");
        transaction.setSide("CR");
        transaction.setAmount(amount);
        transaction.setBalance(newBalance);
        transaction.setTransactionBy(tellerId);
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setRemark("Withdraw Terminal " + terminalId);
        transactionRepository.save(transaction);

        account.setBalance(newBalance);
        return accountRepository.save(account);
    }

    @Transactional
    public Account transfer(String fromAccountNumber, String toAccountNumber, Double amount, String terminalId, String tellerId) throws AccountNotFoundException, TransInsufficientFundException {
        Optional<Account> fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
        Optional<Account> toAccount = accountRepository.findByAccountNumber(toAccountNumber);

        if (fromAccount.isEmpty() || toAccount.isEmpty()) {
            throw new AccountNotFoundException();
        }

        if (fromAccount.get().getBalance() < amount) {
            throw new TransInsufficientFundException();
        }

        double newFromAccountBalance = fromAccount.get().getBalance() - amount;
        Transaction drTrans = new Transaction();
        drTrans.setAccount(fromAccount.get());
        drTrans.setCode("A1");
        drTrans.setType("TRANSFER");
        drTrans.setChannel("ATS");
        drTrans.setSide("CR");
        drTrans.setAmount(amount);
        drTrans.setBalance(newFromAccountBalance);
        drTrans.setRemark("Transfer to x" + toAccount.get().getAccountNumber().substring(4, 7) + " " + toAccount.get().getCustomer().getEngName());
        drTrans.setTransactionDateTime(LocalDateTime.now());
        transactionRepository.save(drTrans);
        fromAccount.get().setBalance(newFromAccountBalance);
        accountRepository.save(fromAccount.get());

        double newToAccountBalance = toAccount.get().getBalance() + amount;
        Transaction crTrans = new Transaction();
        crTrans.setAccount(toAccount.get());
        crTrans.setCode("A1");
        crTrans.setType("TRANSFER");
        crTrans.setChannel("ATS");
        crTrans.setSide("DR");
        crTrans.setAmount(amount);
        crTrans.setBalance(newToAccountBalance);
        crTrans.setRemark("Receive from x" + fromAccount.get().getAccountNumber().substring(4, 7) + " " + fromAccount.get().getCustomer().getEngName());
        crTrans.setTransactionDateTime(LocalDateTime.now());
        transactionRepository.save(crTrans);

        toAccount.get().setBalance(newToAccountBalance);
        accountRepository.save(toAccount.get());

        drTrans.setTransferId(crTrans.getId());
        transactionRepository.save(drTrans);

        crTrans.setTransferId(drTrans.getId());
        transactionRepository.save(crTrans);

        return fromAccount.get();
    }

    public List<Transaction> getTransaction(Long accountId, LocalDateTime fromDate, LocalDateTime toDate) {
        return transactionRepository.findByAccountIdAndTransactionDateTimeBetweenOrderByTransactionDateTime(accountId, fromDate, toDate);
    }
}
