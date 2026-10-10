package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.user.UserStatus;

public record AdminCustomerSummaryDto(Long id,
                                      String firstName,
                                      String lastName,
                                      String email,
                                      UserStatus status) {
}
