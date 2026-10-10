package com.vishnu.banking_application.security.service;

import com.vishnu.banking_application.user.User;

public interface PasswordResetNotificationService {

    void sendResetLink(User user, String rawToken);
}