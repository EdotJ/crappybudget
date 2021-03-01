package com.budgeteer.api.service;


import com.budgeteer.api.annotation.Service;
import com.budgeteer.api.dto.user.SingleUserDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.DuplicateResourceException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.UserRepository;
import com.budgeteer.api.security.PasswordManager;
import io.micronaut.core.util.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    private final PasswordManager passwordManager;

    public UserService(UserRepository repository, PasswordManager passwordManager) {
        this.repository = repository;
        this.passwordManager = passwordManager;
    }

    public Collection<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
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
        return user;
    }

    private void validateUserCreateRequest(SingleUserDto request) {
        validateEmail(request.getEmail());
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
        validateUserUpdateRequest(request);
        User user = this.getById(id);
        user.setEmail(request.getEmail());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordManager.encode(request.getPassword()));
        }
        return repository.update(user);
    }

    private void validateUserUpdateRequest(SingleUserDto request) {
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
        User user = getById(id);
        repository.delete(user);
    }

}
