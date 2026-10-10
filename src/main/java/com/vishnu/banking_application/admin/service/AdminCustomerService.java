package com.vishnu.banking_application.admin.service;

import com.vishnu.banking_application.admin.dto.AdminCustomerResponseDto;
import com.vishnu.banking_application.admin.dto.AdminCustomerStatisticsDto;
import com.vishnu.banking_application.admin.dto.AdminCustomerSummaryDto;
import com.vishnu.banking_application.admin.dto.UpdateCustomerStatusRequestDto;
import com.vishnu.banking_application.admin.exception.InvalidCustomerStatusException;
import com.vishnu.banking_application.admin.mapper.AdminCustomerResponseMapper;
import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.customer.Customer;
import com.vishnu.banking_application.customer.CustomerRepository;
import com.vishnu.banking_application.customer.exception.CustomerNotFoundException;
import com.vishnu.banking_application.user.User;
import com.vishnu.banking_application.user.UserRepository;
import com.vishnu.banking_application.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminCustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AdminCustomerResponseMapper adminCustomerResponseMapper;
    private final AuditLogService auditLogService;
    private static final int PAGE_SIZE = 20;

    public Page<AdminCustomerSummaryDto> getAllCustomers(
            int page,
            String search) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        String normalizedSearch = search == null
                ? null
                : search.trim();

        if (normalizedSearch != null && normalizedSearch.isBlank()) {
            normalizedSearch = null;
        }

        Page<Customer> customers;

        if (normalizedSearch == null) {
            customers = customerRepository.findAll(pageable);
        } else {
            customers = customerRepository.searchCustomers(
                    normalizedSearch,
                    pageable
            );
        }

        return customers.map(
                adminCustomerResponseMapper::toSummaryDto
        );
    }

    public AdminCustomerResponseDto getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(()-> new CustomerNotFoundException("Customer not found with id: " + customerId));

        return adminCustomerResponseMapper.toDto(customer);
    }

    @Transactional
    public void changeUserStatus(
            Long customerId,
            UpdateCustomerStatusRequestDto dto,
            User actor) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + customerId));

        User user = customer.getUser();

        UserStatus currentStatus = user.getStatus();
        UserStatus requestedStatus = dto.getStatus();

        if (currentStatus == requestedStatus) {
            throw new InvalidCustomerStatusException(
                    "Customer already has status: " + requestedStatus);
        }

        if (currentStatus == UserStatus.BLOCKED) {
            throw new InvalidCustomerStatusException(
                    "Blocked customer cannot be reactivated through this operation");
        }

        user.setStatus(requestedStatus);

        userRepository.save(user);

        auditLogService.record(
                actor,
                AuditAction.CHANGE_CUSTOMER_STATUS,
                AuditResourceType.CUSTOMER,
                customer.getId(),
                AuditStatus.SUCCESS,
                "Customer status changed from "
                        + currentStatus
                        + " to "
                        + requestedStatus
        );
    }

    public AdminCustomerStatisticsDto getCustomerStatistics() {

        long totalCustomers = customerRepository.count();

        long activeCustomers =
                customerRepository.countByUser_Status(UserStatus.ACTIVE);

        long suspendedCustomers =
                customerRepository.countByUser_Status(UserStatus.SUSPENDED);

        long blockedCustomers =
                customerRepository.countByUser_Status(UserStatus.BLOCKED);

        return new AdminCustomerStatisticsDto(
                totalCustomers,
                activeCustomers,
                suspendedCustomers,
                blockedCustomers
        );
    }
}
