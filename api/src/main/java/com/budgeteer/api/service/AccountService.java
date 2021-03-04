package com.budgeteer.api.service;

import com.budgeteer.api.config.Service;
import com.budgeteer.api.dto.account.SingleAccountDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.AccountRepository;
import com.budgeteer.api.security.RestrictedResourceHandler;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.utils.SecurityService;

import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService extends RestrictedResourceHandler {

    private final UserService userService;

    private final AccountRepository accRepository;

    public AccountService(AccountRepository repository, UserService userService, SecurityService securityService) {
        super(securityService);
        this.accRepository = repository;
        this.userService = userService;
    }

    public Collection<Account> getAll(Long userId) {
        User user = userService.getById(userId);
        return accRepository.findByUserId(user.getId());
    }

    public Account getSingle(Long id) {
        Optional<Account> optionalAccount = accRepository.findById(id);
        if (optionalAccount.isEmpty()) {
            String defaultMsg = "This account does not exist";
            throw new ResourceNotFoundException("NOT_FOUND", "account", defaultMsg, "Account was not found");
        }
        Account account = optionalAccount.get();
        checkIfCanAccessResource(account.getUser());
        return account;
    }

    public Account create(SingleAccountDto request) {
        validateAccountRequest(request);
        Long userId = this.getAuthenticatedUserId();
        User user = userService.getById(userId);
        Account account = new Account();
        account.setName(request.getName());
        account.setUser(user);
        return accRepository.save(account);
    }

    public Account update(Long id, SingleAccountDto request) {
        validateAccountRequest(request);
        Account account = getSingle(id);
        checkIfCanAccessResource(account.getUser());
        account.setName(request.getName());
        return accRepository.update(account);
    }

    private void validateAccountRequest(SingleAccountDto request) {
        if (!StringUtils.hasText(request.getName())) {
            String defaultMsg = "Account name cannot be empty";
            throw new BadRequestException("BAD_ACCOUNT_NAME", "empty", defaultMsg, "Account name is empty");
        }
    }

    public void delete(Long id) {
        Account account = getSingle(id);
        checkIfCanAccessResource(account.getUser());
        accRepository.delete(account);
    }
}
