package com.issa.poc.controller;

import com.issa.poc.exception.TransInsufficientFundException;
import com.issa.poc.model.Account;
import com.issa.poc.msg.*;
import com.issa.poc.exception.AccountNotFoundException;
import com.issa.poc.exception.DepositAmountLessThanOneException;
import com.issa.poc.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<BaseResMsg> deposit(@RequestBody TransDepWithReqMsg requestMsg) {
        try {
            Account account = transactionService.deposit(requestMsg.getAccountNumber(), requestMsg.getAmount(), requestMsg.getTerminalId(), requestMsg.getTellerId());
            return ResponseEntity.status(HttpStatus.OK).body(new TransSuccessResMsg("Deposit Successful", account));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransErrResMsg(e));
        } catch (DepositAmountLessThanOneException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransErrResMsg(e.STATUS_CODE, e.DESCRIPTION));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TransErrResMsg("500", e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<BaseResMsg> withdraw(@RequestBody TransDepWithReqMsg requestMsg) {
        try {
            Account account = transactionService.withdraw(requestMsg.getAccountNumber(), requestMsg.getAmount(), requestMsg.getTerminalId(), requestMsg.getTellerId());
            return ResponseEntity.status(HttpStatus.OK).body(new TransSuccessResMsg("Withdraw Successful", account));
        } catch (AccountNotFoundException | TransInsufficientFundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransErrResMsg("400", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TransErrResMsg("500", e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<BaseResMsg> transfer(@RequestBody TransTransferReqMsg requestMsg) {
        try {
            Account account = transactionService.transfer(requestMsg.getFromAccountNumber(), requestMsg.getToAccountNumber(), requestMsg.getAmount(), requestMsg.getTerminalId(), requestMsg.getTellerId());
            return ResponseEntity.status(HttpStatus.OK).body(new TransSuccessResMsg("Transfer Successful", account));
        } catch (AccountNotFoundException | TransInsufficientFundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransErrResMsg("400", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TransErrResMsg("500", e.getMessage()));
        }
    }

}
