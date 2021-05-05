package com.budgeteer.api.core;

import com.budgeteer.api.model.User;
import com.budgeteer.api.service.EmailService;
import com.budgeteer.api.service.UserService;
import io.micronaut.context.event.ApplicationEventListener;

import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class RegistrationListener implements ApplicationEventListener<OnRegistrationCompleteEvent> {

    private final UserService userService;

    private final EmailService emailService;

    public RegistrationListener(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token, event.getClientHost());
        String recipientEmail = user.getEmail();
        String subject = "Registration Confirmation on Budget Site";
        String confirmationUrl = "<a>" + event.getClientHost() + "confirm?token=" + token + "</a>";
        String message = "<h1> You're one step closer to managing your finances... </h1>"
                + "<b>Confirm your registration on " + confirmationUrl + "</b>"
                + "<p><i>This message is absolutely shady on purpose </i></p>";

        emailService.sendEmail(recipientEmail, subject, message);
    }
}
