package com.vishnu.banking_application.admin.service;

import com.vishnu.banking_application.admin.dto.AdminBeneficiaryResponseDto;
import com.vishnu.banking_application.admin.dto.AdminBeneficiarySummaryDto;
import com.vishnu.banking_application.admin.dto.UpdateBeneficiaryStatusRequestDto;
import com.vishnu.banking_application.admin.mapper.AdminBeneficiaryMapper;
import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.beneficiary.Beneficiary;
import com.vishnu.banking_application.beneficiary.BeneficiaryRepository;
import com.vishnu.banking_application.beneficiary.BeneficiaryStatus;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryNotFoundException;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryStatusException;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBeneficiaryService {

    private static final int PAGE_SIZE = 20;

    private final BeneficiaryRepository beneficiaryRepository;
    private final AdminBeneficiaryMapper adminBeneficiaryMapper;
    private final AuditLogService auditLogService;

    public Page<AdminBeneficiarySummaryDto> getAllBeneficiaries(int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        return beneficiaryRepository.findAll(pageable)
                .map(adminBeneficiaryMapper::toSummaryDto);
    }

    public AdminBeneficiaryResponseDto getBeneficiary(Long beneficiaryId) {

        Beneficiary beneficiary =
                beneficiaryRepository.findById(beneficiaryId)
                        .orElseThrow(() ->
                                new BeneficiaryNotFoundException(
                                        "Beneficiary not found with id: "
                                                + beneficiaryId
                                ));

        return adminBeneficiaryMapper.toResponseDto(beneficiary);
    }

    @Transactional
    public void changeBeneficiaryStatus(
            Long beneficiaryId,
            UpdateBeneficiaryStatusRequestDto dto,
            User actor) {

        Beneficiary beneficiary =
                beneficiaryRepository.findById(beneficiaryId)
                        .orElseThrow(() ->
                                new BeneficiaryNotFoundException(
                                        "Beneficiary not found with id: "
                                                + beneficiaryId
                                ));

        BeneficiaryStatus currentStatus =
                beneficiary.getStatus();

        BeneficiaryStatus requestedStatus =
                dto.getStatus();

        if (currentStatus == requestedStatus) {
            throw new BeneficiaryStatusException(
                    "Beneficiary already has status: "
                            + requestedStatus
            );
        }

        if (currentStatus == BeneficiaryStatus.REMOVED) {
            throw new BeneficiaryStatusException(
                    "Removed beneficiary status cannot be changed"
            );
        }

        beneficiary.setStatus(requestedStatus);

        auditLogService.record(
                actor,
                AuditAction.CHANGE_BENEFICIARY_STATUS,
                AuditResourceType.BENEFICIARY,
                beneficiary.getId(),
                AuditStatus.SUCCESS,
                "Beneficiary status changed from "
                        + currentStatus
                        + " to "
                        + requestedStatus
        );
    }
}