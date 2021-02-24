package com.budgeteer.api.service;

import com.budgeteer.api.annotation.Service;
import com.budgeteer.api.dto.account.SingleAccountDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.AccountRepository;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthorizationException;

import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    private final UserService userService;

    private final AccountRepository accRepository;

    public AccountService(AccountRepository repository, UserService userService) {
        this.accRepository = repository;
        this.userService = userService;
    }

    public Collection<Account> getAll(Long userId) {
        User user = userService.getById(userId);
        return accRepository.findByUserId(user.getId());
    }

    public Account getSingle(Long id) {
        Optional<Account> account = accRepository.findById(id);
        if (account.isEmpty()) {
            String defaultMsg = "This account does not exist";
            throw new ResourceNotFoundException("NOT_FOUND", "account", defaultMsg, "Account was not found");
        }
        return account.get();
    }

    public Account create(SingleAccountDto request, Authentication principal) {
        validateAccountRequest(request);
        Long userId = (Long) principal.getAttributes().get("id");
        User user = userService.getById(userId);
        Account account = new Account();
        account.setName(request.getName());
        account.setUser(user);
        return accRepository.save(account);
    }

    public Account update(Long id, SingleAccountDto request, Authentication principal) {
        validateAccountRequest(request);
        Account account = getSingle(id);
        Long userId = (Long) principal.getAttributes().get("id");
        if (!userId.equals(account.getUser().getId())) {
            throw new AuthorizationException(principal);
        }
        account.setName(request.getName());
        return accRepository.update(account);
    }

    private void validateAccountRequest(SingleAccountDto request) {
        if (StringUtils.isEmpty(request.getName()) || !StringUtils.hasText(request.getName())) {
            String defaultMsg = "Account name cannot be empty";
            throw new BadRequestException("BAD_ACCOUNT_NAME", "empty", defaultMsg, "Account name is empty");
        }
    }

    public void delete(Long id) {
        Account account = getSingle(id);
        accRepository.delete(account);
    }
}
