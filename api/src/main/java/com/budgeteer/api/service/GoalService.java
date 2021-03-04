package com.budgeteer.api.service;

import com.budgeteer.api.config.Service;
import com.budgeteer.api.dto.goal.SingleGoalDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Goal;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.GoalRepository;
import com.budgeteer.api.security.RestrictedResourceHandler;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.utils.SecurityService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService extends RestrictedResourceHandler {

    private final UserService userService;
    private final AccountService accountService;
    private final GoalRepository repository;

    public GoalService(UserService userService,
                       AccountService accountService,
                       GoalRepository repository,
                       SecurityService securityService) {
        super(securityService);
        this.userService = userService;
        this.accountService = accountService;
        this.repository = repository;
    }

    public List<Goal> getAll() {
        User user = userService.getById(getAuthenticatedUserId());
        return repository.findByUserId(user.getId());
    }

    public Goal getSingle(Long id) {
        Optional<Goal> optionalGoal = repository.findById(id);
        if (optionalGoal.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "goal", "This goal does not exist", "Goal was not found");
        }
        Goal goal = optionalGoal.get();
        checkIfCanAccessResource(goal.getUser());
        return optionalGoal.get();
    }

    public Goal create(SingleGoalDto request) {
        validateGoalRequest(request);
        User user = userService.getById(getAuthenticatedUserId());
        Goal goal = new Goal();
        goal.setName(request.getName());
        if (StringUtils.hasText(request.getDescription())) {
            goal.setDescription(request.getDescription());
        }
        goal.setValue(request.getValue());
        goal.setDate(request.getDate());
        if (request.getAccountId() != null) {
            Account account = accountService.getSingle(request.getAccountId());
            goal.setAccount(account);
        }
        goal.setUser(user);
        return repository.save(goal);
    }

    public Goal update(Long id, SingleGoalDto request) {
        Goal goal = getSingle(id);
        checkIfCanAccessResource(goal.getUser());
        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setDate(request.getDate());
        goal.setValue(request.getValue());
        if (request.getAccountId() != null) {
            Account account = accountService.getSingle(request.getAccountId());
            goal.setAccount(account);
        }
        return repository.update(goal);
    }

    private void validateGoalRequest(SingleGoalDto request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new BadRequestException("BAD_GOAL_NAME", "empty",
                    "Goal name cannot be empty", "Goal name is empty");
        }
        if (request.getDate() == null) {
            throw new BadRequestException("BAD_GOAL_DATE", "empty",
                    "Goal date cannot be empty", "Goal date is empty");
        }
        if (request.getDate().compareTo(LocalDate.now()) < 0) {
            throw new BadRequestException("BAD_GOAL_DATE", "past",
                    "Goal date has already passed", "Goal date is in the past");
        }
        if (request.getValue() == null) {
            throw new BadRequestException("BAD_GOAL_VALUE", "empty",
                    "Goal value cannot be empty", "Goal value is empty");
        }
        if (request.getValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("BAD_GOAL_VALUE", "negative",
                    "Goal value cannot be negative", "Goal value is negative");
        }
    }

    public void delete(Long id) {
        Goal goal = getSingle(id);
        checkIfCanAccessResource(goal.getUser());
        repository.delete(goal);
    }
}
