package com.vishnu.banking_application.audit;

public enum AuditAction {
    LOGIN,
    LOGOUT,
    LOGIN_FAILED,
    PASSWORD_RESET,

    CREATE_ACCOUNT,
    CHANGE_ACCOUNT_STATUS,

    CHANGE_BENEFICIARY_STATUS,

    CREATE_BRANCH,
    CHANGE_BRANCH_STATUS,

    CHANGE_CUSTOMER_STATUS
}
