package com.vishnu.banking_application.branch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByBranchCode(String branchCode);

    @Query("""
        SELECT b.branchCode
        FROM Branch b
        WHERE b.status = :status
        """)
    List<String> findAllBranchCodeByStatus(
            @Param("status") BranchStatus status
    );

    boolean existsByBranchCode(String branchCode);

    long countByStatus(BranchStatus status);

    boolean existsByIfscCode(String ifscCode);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
        SELECT b
        FROM Branch b
        WHERE LOWER(b.branchCode) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(b.branchName) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(b.ifscCode) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    Page<Branch> searchBranches(
            @Param("search") String search,
            Pageable pageable
    );
}