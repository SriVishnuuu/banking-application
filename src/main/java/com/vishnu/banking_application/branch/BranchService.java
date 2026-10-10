package com.vishnu.banking_application.branch;

import com.vishnu.banking_application.admin.dto.AdminBranchStatisticsDto;
import com.vishnu.banking_application.branch.dto.AdminBranchResponseDto;
import com.vishnu.banking_application.branch.dto.AdminBranchSummaryDto;
import com.vishnu.banking_application.branch.dto.CreateBranchRequestDto;
import com.vishnu.banking_application.branch.dto.UpdateBranchStatusRequestDto;
import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.branch.exception.BranchAlreadyExistsException;
import com.vishnu.banking_application.branch.exception.BranchNotFoundException;
import com.vishnu.banking_application.branch.exception.BranchStatusException;
import com.vishnu.banking_application.branch.mapper.AdminBranchMapper;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BranchService {

    private static final int PAGE_SIZE = 20;

    private final BranchRepository branchRepository;
    private final AdminBranchMapper adminBranchMapper;
    private final IfscGenerator ifscGenerator;
    private final AuditLogService auditLogService;


    public Page<AdminBranchSummaryDto> getAllBranches(
            int page,
            String search
    ) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);

        if (search == null || search.isBlank()) {
            return branchRepository.findAll(pageable)
                    .map(adminBranchMapper::toSummaryDto);
        }

        return branchRepository.searchBranches(
                search.trim(),
                pageable
        ).map(adminBranchMapper::toSummaryDto);
    }

    public AdminBranchResponseDto getBranchById(Long branchId) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new BranchNotFoundException("Branch not found"));

        return adminBranchMapper.toResponseDto(branch);
    }

    @Transactional
    public AdminBranchResponseDto createBranch(
            CreateBranchRequestDto dto,
            User actor) {

        String phoneNumber = dto.getPhoneNumber()
                .replaceAll("[\\s-]", "");

        if (phoneNumber.startsWith("+91")) {
            phoneNumber = phoneNumber.substring(3);
        }

        if (branchRepository.existsByBranchCode(dto.getBranchCode())) {
            throw new BranchAlreadyExistsException(
                    "Branch code already exists");
        }

        if (branchRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BranchAlreadyExistsException(
                    "Branch phone number already exists");
        }

        String ifscCode = ifscGenerator.generate();

        Branch branch = new Branch();

        branch.setBranchCode(dto.getBranchCode());
        branch.setBranchName(dto.getBranchName());
        branch.setIfscCode(ifscCode);
        branch.setAddress(dto.getAddress());
        branch.setPhoneNumber(phoneNumber);
        branch.setStatus(BranchStatus.ACTIVE);

        Branch savedBranch = branchRepository.save(branch);

        auditLogService.record(
                actor,
                AuditAction.CREATE_BRANCH,
                AuditResourceType.BRANCH,
                savedBranch.getId(),
                AuditStatus.SUCCESS,
                "Branch created successfully"
        );

        return adminBranchMapper.toResponseDto(savedBranch);
    }

    @Transactional
    public void changeBranchStatus(
            Long branchId,
            UpdateBranchStatusRequestDto dto,
            User actor) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new BranchNotFoundException("Branch not found"));

        BranchStatus currentStatus = branch.getStatus();
        BranchStatus newStatus = dto.getStatus();

        if (currentStatus == newStatus) {
            throw new BranchStatusException(
                    "Branch is already in " + newStatus + " status");
        }

        if (!((currentStatus == BranchStatus.ACTIVE
                && newStatus == BranchStatus.INACTIVE)
                ||
                (currentStatus == BranchStatus.INACTIVE
                        && newStatus == BranchStatus.ACTIVE))) {

            throw new BranchStatusException(
                    "Invalid branch status transition");
        }

        branch.setStatus(newStatus);

        auditLogService.record(
                actor,
                AuditAction.CHANGE_BRANCH_STATUS,
                AuditResourceType.BRANCH,
                branch.getId(),
                AuditStatus.SUCCESS,
                "Branch status changed from "
                        + currentStatus
                        + " to "
                        + newStatus
        );
    }

    @Transactional(readOnly = true)
    public AdminBranchStatisticsDto getBranchStatistics() {

        long totalBranches = branchRepository.count();

        long activeBranches =
                branchRepository.countByStatus(BranchStatus.ACTIVE);

        long inactiveBranches =
                branchRepository.countByStatus(BranchStatus.INACTIVE);

        return new AdminBranchStatisticsDto(
                totalBranches,
                activeBranches,
                inactiveBranches
        );
    }
}