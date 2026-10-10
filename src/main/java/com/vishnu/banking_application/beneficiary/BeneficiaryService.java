package com.vishnu.banking_application.beneficiary;


import com.vishnu.banking_application.account.AccountRepository;
import com.vishnu.banking_application.account.exception.AccountNotFoundException;
import com.vishnu.banking_application.beneficiary.dto.BeneficiaryCreateRequestDto;
import com.vishnu.banking_application.beneficiary.dto.BeneficiaryResponseDto;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryAlreadyExistsException;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryNotFoundException;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryStatusException;
import com.vishnu.banking_application.beneficiary.exception.SelfBeneficiaryException;
import com.vishnu.banking_application.beneficiary.mapper.BeneficiaryRequestMapper;
import com.vishnu.banking_application.beneficiary.mapper.BeneficiaryResponseMapper;
import com.vishnu.banking_application.customer.Customer;
import com.vishnu.banking_application.customer.CustomerRepository;
import com.vishnu.banking_application.customer.exception.CustomerNotFoundException;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    private final CustomerRepository customerRepository;

    private final AccountRepository accountRepository;

    private final BeneficiaryRequestMapper requestMapper;

    private final BeneficiaryResponseMapper responseMapper;

    @Transactional
    public BeneficiaryResponseDto createBeneficiary( BeneficiaryCreateRequestDto beneficiaryCreateRequestDto, User user) {
        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(()->new CustomerNotFoundException("Customer not found!"));

        accountRepository.findByAccountNumberAndBranchIfscCode(beneficiaryCreateRequestDto.getAccountNumber(), beneficiaryCreateRequestDto.getIfscCode()).orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        if(accountRepository.existsByCustomerIdAndAccountNumber(customer.getId(), beneficiaryCreateRequestDto.getAccountNumber())){
            throw new SelfBeneficiaryException("You cannot add your own account as a beneficiary");
        }

        if(beneficiaryRepository.existsByCustomerIdAndAccountNumber(customer.getId(), beneficiaryCreateRequestDto.getAccountNumber())){
            throw new BeneficiaryAlreadyExistsException("Beneficiary already exists!");
        }

        Beneficiary beneficiary = requestMapper.toEntity(beneficiaryCreateRequestDto);
        beneficiary.setCustomer(customer);
        beneficiaryRepository.save(beneficiary);

        return responseMapper.toDto(beneficiary);

    }

    public List<BeneficiaryResponseDto> viewMyBeneficiaries(User user) {

        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(()->new CustomerNotFoundException("Customer not found!"));

        List<BeneficiaryResponseDto> beneficiaries = beneficiaryRepository.findAllByCustomerIdAndStatus(customer.getId(), BeneficiaryStatus.ACTIVE).stream()
                .map(responseMapper::toDto)
                .toList();

        return beneficiaries;
    }

    public BeneficiaryResponseDto viewBeneficiary(User user, Long beneficiaryId) {

        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found!"));

        Beneficiary beneficiary = beneficiaryRepository
                .findByIdAndCustomerId(beneficiaryId, customer.getId())
                .orElseThrow(() ->
                        new BeneficiaryNotFoundException(
                                "Beneficiary was not found"));

        return responseMapper.toDto(beneficiary);
    }

    public BeneficiaryResponseDto removeBeneficiary(
            User user,
            Long beneficiaryId) {

        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found!"));

        Beneficiary beneficiary = beneficiaryRepository
                .findByIdAndCustomerId(
                        beneficiaryId,
                        customer.getId())
                .orElseThrow(() ->
                        new BeneficiaryNotFoundException(
                                "Beneficiary was not found"));

        if (beneficiary.getStatus() != BeneficiaryStatus.ACTIVE) {
            throw new BeneficiaryStatusException(
                    "Beneficiary cannot be removed");
        }

        beneficiary.setStatus(BeneficiaryStatus.REMOVED);

        beneficiaryRepository.save(beneficiary);

        return responseMapper.toDto(beneficiary);
    }
}
