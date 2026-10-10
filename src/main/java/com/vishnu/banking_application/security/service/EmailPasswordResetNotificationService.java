package com.vishnu.banking_application.security.service;

import com.vishnu.banking_application.security.exception.PasswordResetEmailSendingException;
import com.vishnu.banking_application.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailPasswordResetNotificationService
        implements PasswordResetNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.password-reset-url}")
    private String passwordResetUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendResetLink(User user, String rawToken) {

        String resetLink = passwordResetUrl + "?token=" + rawToken;

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("VISHNU BANK - Password Reset");

            String htmlContent = """
                    <html>
                    <body>
                        <h2>Password Reset Request</h2>

                        <p>Hello,</p>

                        <p>
                            We received a request to reset the password
                            for your VISHNU BANK account.
                        </p>

                        <p>
                            Click the button below to reset your password:
                        </p>

                        <p>
                            <a href="%s"
                               style="
                                   display:inline-block;
                                   padding:12px 20px;
                                   background-color:#1a73e8;
                                   color:white;
                                   text-decoration:none;
                                   border-radius:5px;
                               ">
                                Reset Password
                            </a>
                        </p>

                        <p>
                            This link will expire in <strong>15 minutes</strong>
                            and can only be used once.
                        </p>

                        <p>
                            If you did not request a password reset,
                            you can safely ignore this email.
                        </p>

                        <p>
                            Regards,<br>
                            VISHNU BANK
                        </p>
                    </body>
                    </html>
                    """.formatted(resetLink);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException exception) {
            throw new PasswordResetEmailSendingException(
                    "Unable to send password reset email",
                    exception
            );
        }
    }
}