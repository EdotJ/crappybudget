package com.budgeteer.api.service;

import com.budgeteer.api.config.Service;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

@Service
public class EmailService {

    private final Mailer mailer;

    @Value("${emailprovider.server.emailaddress}")
    protected String fromEmail;

    public EmailService(@Value("${emailprovider.server.host}") String host,
                        @Value("${emailprovider.server.port}") int port,
                        @Value("${emailprovider.server.username}") String username,
                        @Value("${emailprovider.server.password}") String password) {
        mailer = MailerBuilder.withSMTPServer(host, port, username, password).buildMailer();
    }

    public void sendEmail(String recipient, String subject, String message) {
        Email email = EmailBuilder.startingBlank()
                .from(fromEmail)
                .to(recipient)
                .withSubject(subject)
                .withHTMLText(message)
                .buildEmail();
        mailer.sendMail(email);
    }
}
