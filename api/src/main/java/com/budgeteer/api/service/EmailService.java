package com.budgeteer.api.service;

import com.budgeteer.api.core.EmailConfig;
import com.budgeteer.api.core.Service;
import io.micronaut.http.server.exceptions.InternalServerException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

@Service
public class EmailService {

    private Mailer mailer;

    private String fromEmail;

    private final boolean isEnabled;

    public EmailService(EmailConfig emailConfig) {
        isEnabled = emailConfig.isEnabled();
        mailer = null;
        fromEmail = "";
        if (emailConfig.isEnabled()) {
            try {
                fromEmail = emailConfig.getEmailAddress();
                mailer = MailerBuilder
                        .withSMTPServer(emailConfig.getHost(),
                                emailConfig.getPort(),
                                emailConfig.getUsername(),
                                emailConfig.getPassword()).buildMailer();
            } catch (Exception e) {
                // we don't want to fail during bean initialization
            }
        } else {
            fromEmail = "budgetsite@genericmail.com";
            mailer = null;
        }
    }

    public void sendEmail(String recipient, String subject, String message) {
        if (mailer != null) {
            Email email = EmailBuilder.startingBlank()
                    .from(fromEmail)
                    .to(recipient)
                    .withSubject(subject)
                    .withHTMLText(message)
                    .buildEmail();
            mailer.sendMail(email);
        } else if (isEnabled) {
            throw new InternalServerException("Failed sending the email. Please try again later");
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
