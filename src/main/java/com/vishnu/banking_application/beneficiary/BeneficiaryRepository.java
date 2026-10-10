package com.vishnu.banking_application.beneficiary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary,Long> {

    boolean existsByCustomerIdAndAccountNumber(Long customerId, String accountNumber);

    List<Beneficiary> findAllByCustomerIdAndStatus(
            Long customerId,
            BeneficiaryStatus status
    );
    Optional<Beneficiary> findByIdAndCustomerId(Long id, Long customerId);

}
