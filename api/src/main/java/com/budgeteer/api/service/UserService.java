package com.budgeteer.api.service;


import com.budgeteer.api.core.OnRegistrationCompleteEvent;
import com.budgeteer.api.core.Service;
import com.budgeteer.api.dto.user.ResendEmailRequest;
import com.budgeteer.api.dto.user.ResetPasswordRequest;
import com.budgeteer.api.dto.user.SingleUserDto;
import com.budgeteer.api.exception.*;
import com.budgeteer.api.model.PasswordResetToken;
import com.budgeteer.api.model.User;
import com.budgeteer.api.model.VerificationToken;
import com.budgeteer.api.repository.PasswordTokenRepository;
import com.budgeteer.api.repository.RefreshTokenRepository;
import com.budgeteer.api.repository.UserRepository;
import com.budgeteer.api.repository.VerificationTokenRepository;
import com.budgeteer.api.security.PasswordManager;
import com.budgeteer.api.security.RestrictedResourceHandler;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.util.StringUtils;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.utils.SecurityService;
import org.apache.commons.validator.routines.EmailValidator;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends RestrictedResourceHandler {

    private final UserRepository repository;

    private final PasswordManager passwordManager;

    private final ApplicationEventPublisher eventPublisher;

    private final String appUrl;

    private final VerificationTokenRepository emailTokenRepository;

    private final PasswordTokenRepository passwordTokenRepository;

    private final EmailService emailService;

    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository repository,
                       PasswordManager passwordManager,
                       SecurityService securityService,
                       ApplicationEventPublisher eventPublisher,
                       EmbeddedServer embeddedServer,
                       VerificationTokenRepository tokenRepository,
                       PasswordTokenRepository passwordTokenRepository,
                       EmailService emailService,
                       RefreshTokenRepository refreshTokenRepository) throws MalformedURLException {
        super(securityService);
        this.repository = repository;
        this.passwordManager = passwordManager;
        this.eventPublisher = eventPublisher;
        appUrl = embeddedServer.getURI().toString() + "/";
        this.emailTokenRepository = tokenRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.emailService = emailService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Collection<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
        if (!getAuthenticatedUserId().equals(id)) {
            throw new AuthorizationException(securityService.getAuthentication().orElse(null));
        }
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "user", "This user does not exist", "User was not found");
        }
        return optionalUser.get();
    }

    public User getByEmail(String email) {
        Optional<User> optionalUser = repository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "user", "This user does not exist", "User was not found");
        }
        return optionalUser.get();
    }

    public User getByUsername(String username) {
        Optional<User> optionalUser = repository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "user", "This user does not exist", "User was not found");
        }
        return optionalUser.get();
    }

    public User create(SingleUserDto request, String host) {
        validateUserCreateRequest(request);
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordManager.encode(request.getPassword()));
//        if (!emailService.isEnabled()) {
//            user.setIsVerified(true);
//        }
        user = repository.save(user);
        String realHost = host == null ? appUrl : host;
        if (!realHost.endsWith("/")) {
            realHost = realHost + "/";
        }
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl, realHost));
        return user;
    }

    private void validateUserCreateRequest(SingleUserDto request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BadRequestException("BAD_USERNAME", "empty", "Please add a username", "Username is empty");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("BAD_PASSWORD", "empty", "Password is mandatory", "Password is empty");
        }
        validateUnique(request);
    }

    public User update(Long id, SingleUserDto request) {
        validateUserUpdateRequest(request, id);
        User user = this.getById(id);
        user.setEmail(request.getEmail());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordManager.encode(request.getPassword()));
        }
        return repository.update(user);
    }

    private void validateUserUpdateRequest(SingleUserDto request, Long id) {
        if (!getAuthenticatedUserId().equals(id)) {
            throw new AuthorizationException(securityService.getAuthentication().orElse(null));
        }
        validatePassword(request.getPassword());
        validateEmail(request.getEmail());
        validateUnique(request);
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BadRequestException("BAD_EMAIL", "empty", "Please add an email address", "Email is empty");
        }
        if (!EmailValidator.getInstance(false, true).isValid(email)) {
            throw new BadRequestException("BAD_EMAIL", "misformatted", "Invalid email format", "Invalid email format");
        }
    }

    private void validateUnique(SingleUserDto request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            String msg = "Email is already in use. Try another one.";
            throw new DuplicateResourceException("BAD_EMAIL", "used", msg, "Duplicate email address");
        }
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            String msg = "Username is already in use. Try another one.";
            throw new DuplicateResourceException("BAD_USERNAME", "used", msg, "Duplicate username");
        }
    }

    public void delete(Long id) {
        if (!getAuthenticatedUserId().equals(id)) {
            throw new AuthorizationException(securityService.getAuthentication().orElse(null));
        }
        User user = getById(id);
        repository.delete(user);
    }

    public void createVerificationToken(User user, String token, String clientHost) {
        VerificationToken entity = new VerificationToken();
        entity.setUser(user);
        entity.setValue(token);
        entity.setClientHost(clientHost);
        entity.setExpiration(LocalDateTime.now().plusMinutes(60 * 24));
        emailTokenRepository.save(entity);
    }

    public String activateUser(String token) {
        Optional<VerificationToken> verificationTokenOptional = emailTokenRepository.findByValue(token);
        if (verificationTokenOptional.isEmpty()) {
            String msg = "Verification token not found";
            throw new ResourceNotFoundException("NOT_FOUND", "token", msg, msg);
        }
        VerificationToken verificationToken = verificationTokenOptional.get();
        if (verificationToken.getExpiration().compareTo(LocalDateTime.now()) < 0) {
            String msg = "Verification period has passed. Do you want to try again?";
            String detail = "Verification period has passed";
            throw new TokenExpiredException("VERIFICATION_TOKEN", "expired", msg, detail);
        }
        User user = verificationToken.getUser();
        user.setIsVerified(true);
        repository.update(user);
        String clientHost = verificationToken.getClientHost();
        emailTokenRepository.delete(verificationToken);
        return clientHost;
    }

    public void resendConfirmationEmail(ResendEmailRequest request) {
        User user;
        try {
            user = this.getByEmail(request.getEmail());
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl, request.getClientHost()));
        } catch (ResourceNotFoundException ignored) {
            // We want to avoid showing if a user exists
        }
    }

    public void initiatePasswordReset(String email) {
        User user;
        try {
            user = getByEmail(email);
        } catch (ResourceNotFoundException e) {
            // We want to avoid showing a user exists
            return;
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordToken = new PasswordResetToken(user, token);
        passwordTokenRepository.save(passwordToken);
        String subject = "Reset Your Password on Budget Site";
        emailService.sendEmail(passwordToken.getUser().getEmail(), subject, getMessage(token));
    }

    private String getMessage(String token) {
        return "<h1> You have asked us to reset your password... </h1>"
                + "<b> If this was not you - please change your password immediately.</b>"
                + "<p> Otherwise, you can reset your password by inputting this token: " + token + " </p>";
    }

    public void resetPassword(ResetPasswordRequest request) {
        validatePassword(request.getPassword());
        Optional<PasswordResetToken> passwordTokenOptional = passwordTokenRepository.findByValue(request.getToken());
        if (passwordTokenOptional.isEmpty()) {
            String msg = "Password reset token not found";
            throw new ResourceNotFoundException("NOT_FOUND", "token", msg, msg);
        }
        PasswordResetToken passwordToken = passwordTokenOptional.get();
        if (passwordToken.getExpiration().compareTo(LocalDateTime.now()) < 0) {
            String msg = "Password reset token is expired. Try again";
            String detail = "Expired password reset token";
            throw new TokenExpiredException("PASSWORD_RESET_TOKEN", "expired", msg, detail);
        }
        User user = passwordToken.getUser();
        user.setPassword(passwordManager.encode(request.getPassword()));
        repository.update(user);
        passwordTokenRepository.delete(passwordToken);
    }

    public void clearAllRefreshTokens() {
        User user = getById(getAuthenticatedUserId());
        refreshTokenRepository.deleteByUser(user);
    }

    private void validatePassword(String password) {
        if (password != null && password.length() < 16) {
            String msg = "Password should be at least 16 characters";
            throw new PasswordValidationException("short", msg, "Password too short");
        }
    }
}
