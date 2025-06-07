package com.issa.poc.controller;

import com.issa.poc.exception.AccountNotFoundException;
import com.issa.poc.exception.InvalidStatementQueryException;
import com.issa.poc.model.Account;
import com.issa.poc.exception.CustomerNotFoundException;
import com.issa.poc.exception.AccountInvalidCustomerNameException;
import com.issa.poc.model.Customer;
import com.issa.poc.model.Statement;
import com.issa.poc.msg.AcctErrResMsg;
import com.issa.poc.msg.AcctReqMsg;
import com.issa.poc.msg.AcctOkResMsg;
import com.issa.poc.msg.BaseResMsg;
import com.issa.poc.repository.CustomerRepository;
import com.issa.poc.security.JwtUtil;
import com.issa.poc.security.MyUserDetailsService;
import com.issa.poc.service.AccountService;
import com.issa.poc.exception.DepositAmountLessThanOneException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/add")
    public ResponseEntity<BaseResMsg> addAccount(@RequestBody AcctReqMsg requestMsg) {
        try {
            Account account = accountService.addNewAccount(requestMsg.getCid(), requestMsg.getThaiName(), requestMsg.getEngName(), requestMsg.getTellerId());
            return ResponseEntity.status(HttpStatus.OK).body(new AcctOkResMsg("Create Account Successful", account));
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AcctErrResMsg(e.STATUS_CODE, e.DESCRIPTION));
        } catch (AccountInvalidCustomerNameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AcctErrResMsg(e.STATUS_CODE, e.DESCRIPTION));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AcctErrResMsg("500", e.getMessage()));
        }
    }

    @PostMapping("/addWithDeposit")
    public ResponseEntity<BaseResMsg> addAccountWithDeposit(@RequestBody AcctReqMsg requestMsg) {
        try {
            Account account = accountService.addNewAccountWithDeposit(requestMsg.getCid(), requestMsg.getThaiName(),
                    requestMsg.getEngName(), requestMsg.getDepositAmount(), requestMsg.getTellerId(), requestMsg.getTerminalId());
            return ResponseEntity.status(HttpStatus.OK).body(new AcctOkResMsg("Create Account Successful", account));
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AcctErrResMsg(e.STATUS_CODE, e.DESCRIPTION));
        } catch (AccountInvalidCustomerNameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AcctErrResMsg(e.STATUS_CODE, e.DESCRIPTION));
        } catch (DepositAmountLessThanOneException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AcctErrResMsg(e.STATUS_CODE, e.DESCRIPTION));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AcctErrResMsg("500", e.getMessage()));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<BaseResMsg> getInfo(@RequestHeader("Authorization") String authHeader, @RequestParam String accountNumber) {
        try {
            Customer customer = userDetailsService.getCustomerFromAuthHeader(authHeader);
            Account account = accountService.getAccount(accountNumber);

            if(account.getCustomer() == customer) {
                return ResponseEntity.status(HttpStatus.OK).body(new AcctOkResMsg("Get Account Info Successful", account));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AcctErrResMsg(e.STATUS_CODE, e.DESCRIPTION));
        }
    }

    @GetMapping("/statement")
    public ResponseEntity<Statement> getStatement(@RequestHeader("Authorization") String authHeader, @RequestParam String accountNumber, @RequestParam String yearMonth) {
        try {
            Customer customer = userDetailsService.getCustomerFromAuthHeader(authHeader);
            Account account = accountService.getAccount(accountNumber);
            if(account.getCustomer() == customer) {
                String from = yearMonth + "01";
                String to = getLastDayOfMonth(yearMonth);

                return ResponseEntity.status(HttpStatus.OK).body(accountService.getStatement(accountNumber, from, to));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

        } catch (InvalidStatementQueryException | AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    public String getLastDayOfMonth(String yearMonth) {
        String year = yearMonth.substring(0, 4);
        String month = yearMonth.substring(4, 6);

        if (month.equals("01") || month.equals("03") || month.equals("05") || month.equals("07") || month.equals("08") || month.equals("12")) {
            return yearMonth + "31";
        } else if (month.equals("04") || month.equals("06") || month.equals("09") || month.equals("11")) {
            return yearMonth + "30";
        } else if (month.equals("02")){
            if(Integer.parseInt(year)%4==0) {
                return yearMonth + "29";
            } else {
                return yearMonth + "28";
            }
        } else {
            return "";
        }
    }
}
