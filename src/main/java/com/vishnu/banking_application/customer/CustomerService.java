package com.vishnu.banking_application.customer;


import com.vishnu.banking_application.customer.dto.CustomerProfileRequestDto;
import com.vishnu.banking_application.customer.dto.CustomerProfileResponseDto;
import com.vishnu.banking_application.customer.dto.CustomerProfileUpdateRequestDto;
import com.vishnu.banking_application.customer.exception.CustomerAlreadyExistsException;
import com.vishnu.banking_application.customer.exception.CustomerNotFoundException;
import com.vishnu.banking_application.customer.exception.PhoneNumberAlreadyExistsException;
import com.vishnu.banking_application.customer.mapper.CustomerRequestMapper;
import com.vishnu.banking_application.customer.mapper.CustomerResponseMapper;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerRequestMapper mapper;

    private final CustomerResponseMapper responseMapper;

    @Transactional(readOnly = true)
    public CustomerProfileResponseDto viewProfile(User user){
        Long userId = user.getId();
        Customer customer = customerRepository.findByUserId(userId).orElseThrow(() -> new CustomerNotFoundException("Customer was not found"));

        return responseMapper.toDto(customer);
    }

    @Transactional
    public CustomerProfileResponseDto createProfile(CustomerProfileRequestDto customerProfileRequestDto, User user) {
        Long userId = user.getId();

        if(customerRepository.existsByUserId(userId)){

            throw new CustomerAlreadyExistsException("Customer profile  already exists! ");
        }

        if(customerRepository.existsByPhoneNumber(customerProfileRequestDto.getPhoneNumber())){
            throw new PhoneNumberAlreadyExistsException("Phone number already exists!");
        }
        Customer customer = mapper.toEntity(customerProfileRequestDto);
        customer.setUser(user);

        customerRepository.save(customer);
        return responseMapper.toDto(customer);
    }

    @Transactional
    public CustomerProfileResponseDto updateProfile(CustomerProfileUpdateRequestDto dto, User user) {
        Long userId = user.getId();
        Customer customer = customerRepository.findByUserId(userId).orElseThrow(() -> new CustomerNotFoundException("Customer was not found"));

        if(customerRepository.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), customer.getId())){
            throw new PhoneNumberAlreadyExistsException("Phone number already exists!");
        }
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setAddress(dto.getAddress());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customerRepository.save(customer);

        return responseMapper.toDto(customer);
    }
}
