package com.inturn.suncomputer.user.service;

import com.inturn.suncomputer.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetEmailService {

    private final JavaMailSender mailSender;

    private final String fromEmail;

    private final String frontendUrl;

    public PasswordResetEmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromEmail,
            @Value("${app.frontend.url}") String frontendUrl
    ) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.frontendUrl = frontendUrl;
    }

    public void sendPasswordResetEmail(
            User user,
            String rawToken
    ) {

        String resetUrl =
                frontendUrl
                        + "/reset-password?token="
                        + rawToken;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("SunComputer Password Reset");

        message.setText(
                "Hello "
                        + user.getFirstName()
                        + ",\n\n"
                        + "We received a request to reset your SunComputer password.\n\n"
                        + "Use the following link to create a new password:\n\n"
                        + resetUrl
                        + "\n\n"
                        + "This link will expire in 30 minutes.\n\n"
                        + "If you did not request a password reset, you can ignore this email.\n\n"
                        + "Regards,\n"
                        + "SunComputer"
        );

        mailSender.send(message);
    }
}