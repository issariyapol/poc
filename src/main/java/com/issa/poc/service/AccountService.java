package com.issa.poc.service;

import com.issa.poc.exception.*;
import com.issa.poc.model.*;
import com.issa.poc.repository.AccountRepository;
import com.issa.poc.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionService transactionService;

    public Account addNewAccountWithDeposit(String cid, String thaiName, String engName, Double depositAmount, String tellerId, String terminalId) throws CustomerNotFoundException, AccountInvalidCustomerNameException, DepositAmountLessThanOneException {
        Account account = addNewAccount(cid, thaiName, engName, tellerId);
        account = transactionService.deposit(account, depositAmount, terminalId, tellerId);
        return account;
    }

    public Account addNewAccount(String cid, String thaiName, String engName, String tellerId) throws CustomerNotFoundException, AccountInvalidCustomerNameException {
        Account account = new Account();
        account.setBalance(0.00);
        account.setStatus("ACTIVE");

        Optional<Customer> customer = customerRepository.findByCid(cid);

        if (customer.isEmpty()) {
            if (thaiName.isEmpty() || engName.isEmpty()) {
                throw new AccountInvalidCustomerNameException();
            }

            Customer newCustomer = new Customer();
            newCustomer.setCid(cid);
            newCustomer.setThaiName(thaiName);
            newCustomer.setEngName(engName);
            newCustomer.setCreateBy(tellerId);
            customerRepository.save(newCustomer);
            account.setCustomer(newCustomer);
        } else {
            account.setCustomer(customer.get());
        }

        accountRepository.save(account);
        account.setAccountNumber(getAccountNumberFromId(account.getId()));
        accountRepository.save(account);
        return account;
    }

    public String getAccountNumberFromId(Long accountId){
        return String.format("%07d", accountId);
    }

    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

        if(account.isEmpty()){
            throw new AccountNotFoundException();
        }

        return account.get();
    }

    public Statement getStatement(String accountNumber, String from, String to) throws AccountNotFoundException, InvalidStatementQueryException {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

        if(account.isEmpty()){
            throw new AccountNotFoundException();
        }

        Statement statement = new Statement();
        statement.setBalance(account.get().getBalance());
        statement.setValueAsOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss" )));

        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        LocalDateTime fromDateTime = LocalDateTime.parse(from+" 00:00:00",format);
        LocalDateTime toDateTime = LocalDateTime.parse(to+" 00:00:00",format);

        if(fromDateTime.isAfter(toDateTime)){
            throw new InvalidStatementQueryException();
        }

        List<Transaction> transactions = transactionService.getTransaction(account.get().getId(), fromDateTime, toDateTime);
        ArrayList<StatementRecord> records = new ArrayList<>();
        for (Transaction tran : transactions){
            StatementRecord record = new StatementRecord();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String date = tran.getTransactionDateTime().format(dateFormatter);
            String time = tran.getTransactionDateTime().format(timeFormatter);

            record.setDate(date);
            record.setTime(time);
            record.setCode(tran.getCode());
            record.setChannel(tran.getChannel());

            DecimalFormat df = new DecimalFormat("0.00");
            if(tran.getSide().equals("DR")){
                record.setDebitCredit("+"+ df.format(tran.getAmount()));
            } else {
                record.setDebitCredit("-"+ df.format(tran.getAmount()));
            }

            record.setBalance(df.format(tran.getBalance()));
            record.setRemark(tran.getRemark());
            records.add(record);
        }
        statement.setStatementRecords(records);
        return statement;
    }
}
