package com.budgeteer.api.service;

import com.budgeteer.api.core.EmailConfig;
import com.budgeteer.api.core.Service;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

@Service
public class EmailService {

    private final Mailer mailer;

    private final String fromEmail;

    private final boolean isEnabled;

    public EmailService(EmailConfig emailConfig) {
        isEnabled = emailConfig.isEnabled();
        if (emailConfig.isEnabled()) {
            EmailConfig.ServerConfig serverConfig = emailConfig.getServerConfig();
            fromEmail = serverConfig.getEmailAddress();
            mailer = MailerBuilder
                    .withSMTPServer(serverConfig.getHost(),
                            serverConfig.getPort(),
                            serverConfig.getUsername(),
                            serverConfig.getPassword()).buildMailer();
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
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
