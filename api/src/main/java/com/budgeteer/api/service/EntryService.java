package com.budgeteer.api.service;

import com.budgeteer.api.core.Pair;
import com.budgeteer.api.core.Service;
import com.budgeteer.api.dto.entry.SingleEntryDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.EntryRepository;
import com.budgeteer.api.security.RestrictedResourceHandler;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.security.utils.SecurityService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EntryService extends RestrictedResourceHandler {

    private final EntryRepository entryRepository;

    private final UserService userService;

    private final AccountService accountService;

    private final CategoryService categoryService;

    public EntryService(EntryRepository entryRepository,
                        UserService userService,
                        AccountService accountService,
                        CategoryService categoryService,
                        SecurityService securityService) {
        super(securityService);
        this.entryRepository = entryRepository;
        this.userService = userService;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    public List<Entry> getAllByAccount(Long accountId) {
        return this.getAllByAccount(accountId, null).getContent();
    }

    public Page<Entry> getAllByAccount(Long accountId, Pageable page) {
        return this.getAllByAccount(accountId, null, null, page);
    }

    public List<Entry> getAllByAccount(Long accountId, LocalDate from, LocalDate to) {
        return this.getAllByAccount(accountId, from, to, null).getContent();
    }

    public Page<Entry> getAllByAccount(Long accountId, LocalDate from, LocalDate to, Pageable page) {
        Account account = accountService.getSingle(accountId);
        checkIfCanAccessResource(account.getUser());
        if (from != null && to != null) {
            return entryRepository.findByAccountIdAndDateBetweenOrderByDateDesc(account.getId(), from, to, page);
        } else if (from != null) {
            return entryRepository
                    .findByAccountIdAndDateBetweenOrderByDateDesc(account.getId(), from, LocalDate.now(), page);
        } else {
            return entryRepository.findByAccountIdOrderByDateDesc(account.getId(), page);
        }
    }

    public List<Entry> getAllByUser() {
        User user = userService.getById(getAuthenticatedUserId());
        return entryRepository.findByUserIdOrderByDateDesc(user.getId());
    }

    public Page<Entry> getAllByUser(Pageable page) {
        User user = userService.getById(getAuthenticatedUserId());
        return entryRepository.findByUserIdOrderByDateDesc(user.getId(), page);
    }

    public Entry getSingle(Long id) {
        Optional<Entry> optionalEntry = entryRepository.findById(id);
        if (optionalEntry.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "entry", "This entry does not exist", "Entry not found");
        }
        Entry entry = optionalEntry.get();
        checkIfCanAccessResource(entry.getUser());
        return optionalEntry.get();
    }

    public Entry create(SingleEntryDto request) {
        validateEntryRequest(request);
        Account account = accountService.getSingle(request.getAccountId());
        User user = account.getUser();
        checkIfCanAccessResource(user);
        Category category = categoryService.getSingle(request.getCategoryId());
        // TODO: reduce ridiculous construction
        Entry entry = new Entry();
        setEntryFromRequest(request, entry, user);
        entry.setCategory(category);
        entry.setAccount(account);
        return entryRepository.save(entry);
    }

    public Entry update(Long id, SingleEntryDto request) {
        validateEntryRequest(request);
        Entry entry = getSingle(id);
        User user = entry.getUser();
        checkIfCanAccessResource(user);
        Category category = entry.getCategory();
        if (category == null || !request.getCategoryId().equals(category.getId())) {
            category = categoryService.getSingle(request.getCategoryId());
        }
        Account account = entry.getAccount();
        if (!request.getAccountId().equals(account.getId())) {
            account = accountService.getSingle(request.getAccountId());
        }
        setEntryFromRequest(request, entry, user);
        entry.setAccount(account);
        entry.setCategory(category);
        return entryRepository.update(entry);
    }

    private void setEntryFromRequest(SingleEntryDto request, Entry entry, User user) {
        entry.setName(request.getName());
        entry.setDescription(request.getDescription());
        entry.setValue(request.getValue());
        entry.setDate(request.getDate());
        entry.setIsExpense(request.isExpense() == null || request.isExpense());
        entry.setUser(user);
    }

    public void saveAllEntries(List<Entry> entries) {
        entryRepository.saveAll(entries);
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
        checkIfCanAccessResource(entry.getUser());
        entryRepository.delete(entry);
    }

    public Pair<BigDecimal, BigDecimal> getMonthlyBalance(Long id) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<Entry> entries = this.getAllByAccount(id, start, end);
        Pair<BigDecimal, BigDecimal> pair = new Pair<>(BigDecimal.ZERO, BigDecimal.ZERO);
        for (Entry entry : entries) {
            if (entry.isExpense()) {
                pair.setFirst(pair.getFirst().add(entry.getValue()));
            } else {
                pair.setSecond(pair.getSecond().add(entry.getValue()));
            }
        }
        return pair;
    }

    public BigDecimal getBalance() {
        User user = userService.getById(getAuthenticatedUserId());
        return entryRepository.findSumValueByUserId(user.getId()).orElse(BigDecimal.ZERO);
    }
}
