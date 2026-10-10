package com.vishnu.banking_application.customer;

import com.vishnu.banking_application.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByPhoneNumber(String number);

    long countByUser_Status(UserStatus status);

    boolean existsByPhoneNumberAndIdNot(String number, Long id);

    @Query("""
        SELECT c
        FROM Customer c
        JOIN c.user u
        WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
           OR c.phoneNumber LIKE CONCAT('%', :search, '%')
        """)
    Page<Customer> searchCustomers(
            @Param("search") String search,
            Pageable pageable
    );
}
