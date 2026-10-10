package com.vishnu.banking_application.customer;


import com.vishnu.banking_application.customer.dto.CustomerProfileRequestDto;
import com.vishnu.banking_application.customer.dto.CustomerProfileResponseDto;
import com.vishnu.banking_application.customer.dto.CustomerProfileUpdateRequestDto;
import com.vishnu.banking_application.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponseDto> viewProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(customerService.viewProfile(userDetails.getUser()), HttpStatus.OK);
    }

    @PostMapping("/profile")
    public ResponseEntity<CustomerProfileResponseDto> createProfile( @Valid @RequestBody CustomerProfileRequestDto customerProfileRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(customerService.createProfile(customerProfileRequestDto,userDetails.getUser()),HttpStatus.CREATED);
    }

    @PutMapping("/profile")
    public ResponseEntity<CustomerProfileResponseDto> updateProfile(@Valid @RequestBody CustomerProfileUpdateRequestDto customerProfileUpdateRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(customerService.updateProfile(customerProfileUpdateRequestDto, userDetails.getUser()), HttpStatus.OK);
    }

}
