package com.budgeteer.api.service;

import com.budgeteer.api.annotation.Service;
import com.budgeteer.api.dto.entry.SingleEntryDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.EntryRepository;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthorizationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EntryService {

    private final EntryRepository entryRepository;

    private final UserService userService;

    private final AccountService accountService;

    private final CategoryService categoryService;

    public EntryService(EntryRepository entryRepository,
                        UserService userService,
                        AccountService accountService,
                        CategoryService categoryService) {
        this.entryRepository = entryRepository;
        this.userService = userService;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    public List<Entry> getAllByAccount(Long accountId) {
        Account account = accountService.getSingle(accountId);
        return entryRepository.findByAccountId(account.getId());
    }

    public List<Entry> getAllByUser(Long userId) {
        User user = userService.getById(userId);
        return entryRepository.findByUserId(user.getId());
    }

    public Entry getSingle(Long id) {
        Optional<Entry> entry = entryRepository.findById(id);
        if (entry.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "entry", "This entry does not exist", "Entry not found");
        }
        return entry.get();
    }

    public Entry create(SingleEntryDto request) {
        validateEntryRequest(request);
        Account account = accountService.getSingle(request.getAccountId());
        User user = account.getUser();
        Category category = categoryService.getSingle(request.getCategoryId());
        // TODO: reduce ridiculous construction
        Entry entry = new Entry();
        entry.setName(request.getName());
        entry.setDescription(request.getDescription());
        entry.setValue(request.getValue());
        entry.setDate(request.getDate());
        entry.setIsExpense(request.isExpense() == null || request.isExpense());
        entry.setUser(user);
        entry.setCategory(category);
        entry.setAccount(account);
        return entryRepository.save(entry);
    }

    public Entry update(Long id, SingleEntryDto request, Authentication principal) {
        validateEntryRequest(request);
        Entry entry = getSingle(id);
        User user = entry.getUser();
        Long userId = (Long) principal.getAttributes().get("id");
        if (!userId.equals(user.getId())) {
            throw new AuthorizationException(principal);
        }
        Category category = entry.getCategory();
        if (!request.getCategoryId().equals(category.getId())) {
            category = categoryService.getSingle(request.getCategoryId());
        }
        Account account = entry.getAccount();
        if (!request.getAccountId().equals(account.getId())) {
            account = accountService.getSingle(request.getAccountId());
        }
        entry.setName(request.getName());
        entry.setDescription(request.getDescription());
        entry.setValue(request.getValue());
        entry.setIsExpense(request.isExpense() == null || request.isExpense());
        entry.setUser(user);
        entry.setAccount(account);
        entry.setCategory(category);
        return entryRepository.update(entry);
    }

    private void validateEntryRequest(SingleEntryDto request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new BadRequestException("BAD_ENTRY_NAME", "empty", "Please add an entry name", "Entry name is empty");
        }
        if (request.getValue() == null) {
            String msg = "Please add an entry value";
            throw new BadRequestException("BAD_ENTRY_VALUE", "empty", msg, "Entry value is empty");
        }
        if (request.getValue().compareTo(BigDecimal.ZERO) < 0) {
            String msg = "Entry value cannot be negative";
            throw new BadRequestException("BAD_ENTRY_VALUE", "negative", msg, "Entry value is negative");
        }
        if (request.getDate() == null) {
            String msg = "Please provide an entry date";
            throw new BadRequestException("BAD_ENTRY_DATE", "empty", msg, "Entry date not provided");
        }
        if (request.getAccountId() == null) {
            String msg = "Please select an account";
            throw new BadRequestException("BAD_ENTRY", "no_account_id", msg, "Account identifier not provided");
        }
        if (request.getCategoryId() == null) {
            String msg = "Please select a category";
            throw new BadRequestException("BAD_ENTRY", "no_category_id", msg, "Category identifier not provided");
        }
    }

    public void delete(Long id) {
        Entry entry = getSingle(id);
        entryRepository.delete(entry);
    }
}
