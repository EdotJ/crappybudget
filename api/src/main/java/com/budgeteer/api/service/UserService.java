package com.budgeteer.api.service;


import com.budgeteer.api.annotation.Service;
import com.budgeteer.api.dto.user.SingleUserDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.UserRepository;
import io.micronaut.core.util.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Collection<User> getAll() {
        return repository.findAll();
    }

    public User getSingle(Long id) {
        Optional<User> optionalUser = repository.findById(id);
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
        // TODO: don't save passwords in plaintext
        user.setPassword(request.getPassword());
        user = repository.save(user);
        return user;
    }

    private void validateUserCreateRequest(SingleUserDto request) {
        validateEmail(request.getEmail());
        if (StringUtils.isEmpty(request.getUsername()) || !StringUtils.hasText(request.getUsername())) {
            throw new BadRequestException("BAD_USERNAME", "empty", "Please add a username", "Username is empty");
        }
        if (StringUtils.isEmpty(request.getPassword()) || !StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("BAD_PASSWORD", "empty", "Password is mandatory", "Password is empty");
        }
        // TODO: password validation
    }

    public User update(Long id, SingleUserDto request) {
        validateUserUpdateRequest(request);
        User user = this.getSingle(id);
        user.setEmail(request.getEmail());
        return repository.update(user);
    }

    private void validateUserUpdateRequest(SingleUserDto request) {
        validateEmail(request.getEmail());
    }

    private void validateEmail(String email) {
        if (StringUtils.isEmpty(email) || !StringUtils.hasText(email)) {
            throw new BadRequestException("BAD_EMAIL", "empty", "Please add an email address", "Email is empty");
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequestException("BAD_EMAIL", "misformatted", "Invalid email format", "Invalid email format");
        }
    }

    public void delete(Long id) {
        User user = getSingle(id);
        repository.delete(user);
    }

}
