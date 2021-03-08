package com.budgeteer.api.service;


import com.budgeteer.api.config.OnRegistrationCompleteEvent;
import com.budgeteer.api.config.Service;
import com.budgeteer.api.dto.user.ResendEmailRequest;
import com.budgeteer.api.dto.user.SingleUserDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.DuplicateResourceException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.exception.VerificationTokenExpiredException;
import com.budgeteer.api.model.User;
import com.budgeteer.api.model.VerificationToken;
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
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService extends RestrictedResourceHandler {

    private final UserRepository repository;

    private final PasswordManager passwordManager;

    private final ApplicationEventPublisher eventPublisher;

    private final String appUrl;

    private final VerificationTokenRepository emailTokenRepository;

    public UserService(UserRepository repository,
                       PasswordManager passwordManager,
                       SecurityService securityService,
                       ApplicationEventPublisher eventPublisher,
                       EmbeddedServer embeddedServer,
                       VerificationTokenRepository tokenRepository) throws MalformedURLException {
        super(securityService);
        this.repository = repository;
        this.passwordManager = passwordManager;
        this.eventPublisher = eventPublisher;
        appUrl = new URL("http", embeddedServer.getHost(), "/").toString();
        this.emailTokenRepository = tokenRepository;
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

    public User create(SingleUserDto request) {
        validateUserCreateRequest(request);
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordManager.encode(request.getPassword()));
        user = repository.save(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl));
        return user;
    }

    private void validateUserCreateRequest(SingleUserDto request) {
        validateEmail(request.getEmail());
        // TODO: validate length
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BadRequestException("BAD_USERNAME", "empty", "Please add a username", "Username is empty");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("BAD_PASSWORD", "empty", "Password is mandatory", "Password is empty");
        }
        validateUnique(request);
        // TODO: password validation
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
        validateEmail(request.getEmail());
        validateUnique(request);
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BadRequestException("BAD_EMAIL", "empty", "Please add an email address", "Email is empty");
        }
        if (!EmailValidator.getInstance().isValid(email)) {
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

    public void createVerificationToken(User user, String token) {
        VerificationToken entity = new VerificationToken();
        entity.setUser(user);
        entity.setValue(token);
        entity.setExpiration(LocalDateTime.now().plusMinutes(60 * 24));
        emailTokenRepository.save(entity);
    }

    public void activateUser(String token) {
        Optional<VerificationToken> verificationTokenOptional = emailTokenRepository.findByValue(token);
        if (verificationTokenOptional.isEmpty()) {
            String msg = "Verification token not found";
            throw new ResourceNotFoundException("NOT_FOUND", "token", msg, msg);
        }
        VerificationToken verificationToken = verificationTokenOptional.get();
        if (verificationToken.getExpiration().compareTo(LocalDateTime.now()) < 0) {
            throw new VerificationTokenExpiredException();
        }
        User user = verificationToken.getUser();
        user.setIsVerified(true);
        repository.update(user);
    }

    public void resendConfirmationEmail(ResendEmailRequest request) {
        User user;
        try {
            user = this.getByEmail(request.getEmail());
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl));
        } catch (ResourceNotFoundException ignored) {
            // We want to avoid showing if a user exists
        }
    }
}
