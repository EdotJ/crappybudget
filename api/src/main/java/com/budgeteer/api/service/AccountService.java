package com.budgeteer.api.service;

import com.budgeteer.api.annotation.Service;
import com.budgeteer.api.dto.account.SingleAccountDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.AccountRepository;
import io.micronaut.core.util.StringUtils;

import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    UserService userService;

    AccountRepository accRepository;

    public AccountService(AccountRepository repository, UserService userService) {
        this.accRepository = repository;
        this.userService = userService;
    }

    public Collection<Account> getAll(Long userId) {
        User user = userService.getSingle(userId);
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

    public Account create(SingleAccountDto request) {
        validateAccountRequest(request);
        User user = userService.getSingle(request.getUserId());
        Account account = new Account();
        account.setName(request.getName());
        account.setUser(user);
        return accRepository.save(account);
    }

    public Account update(Long id, SingleAccountDto request) {
        validateAccountRequest(request);
        Account account = getSingle(id);
        User newUser = null;
        if (!request.getUserId().equals(account.getUser().getId())) {
            newUser = userService.getSingle(request.getUserId());
        }
        account.setName(request.getName());
        if (newUser != null) {
            account.setUser(newUser);
        }
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
