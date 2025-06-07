package com.issa.poc.controller;

import com.issa.poc.model.Customer;
import com.issa.poc.exception.BadRequestRegisterException;
import com.issa.poc.msg.RegisterResponseMsg;
import com.issa.poc.msg.RegisterSuccessResMsg;
import com.issa.poc.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseMsg> registerCustomer(@RequestBody Customer customer){
        try{
            registerService.newCustomer(customer);
            return ResponseEntity.ok(new RegisterSuccessResMsg());
        }catch(BadRequestRegisterException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegisterResponseMsg("400", e.getMessage()));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RegisterResponseMsg("500", e.getMessage()));
        }
    }
}
