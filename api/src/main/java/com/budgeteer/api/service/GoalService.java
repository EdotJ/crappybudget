package com.budgeteer.api.service;

import com.budgeteer.api.core.Service;
import com.budgeteer.api.dto.goal.SingleGoalDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.*;
import com.budgeteer.api.repository.GoalRepository;
import com.budgeteer.api.repository.GoalTypeRepository;
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
    private final CategoryService categoryService;
    private final GoalRepository repository;
    private final GoalTypeRepository goalTypeRepository;

    public GoalService(UserService userService,
                       AccountService accountService,
                       CategoryService categoryService,
                       GoalRepository repository,
                       SecurityService securityService,
                       GoalTypeRepository goalTypeRepository) {
        super(securityService);
        this.userService = userService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.repository = repository;
        this.goalTypeRepository = goalTypeRepository;
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
        Optional<GoalType> goalType = getGoalType(request);
        goal.setGoalType(goalType.get());
        goal.setCurrentValue(BigDecimal.ZERO);
        goal.setGoalValue(request.getGoalValue());
        goal.setDate(request.getDate());
        setAccountIfNotNull(request, goal);
        setCategoryIfNotNull(request, goal);
        goal.setUser(user);
        return repository.save(goal);
    }

    private Optional<GoalType> getGoalType(SingleGoalDto request) {
        Long goalTypeId = request.getGoalType();
        if (goalTypeId == null) {
            goalTypeId = 1L;
        }
        Optional<GoalType> goalType = goalTypeRepository.findById(goalTypeId);
        if (goalType.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "goal_type", "Goal type not found", "Goal type not found");
        }
        return goalType;
    }

    public Goal update(Long id, SingleGoalDto request) {
        Goal goal = getSingle(id);
        checkIfCanAccessResource(goal.getUser());
        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setDate(request.getDate());
        goal.setGoalValue(request.getGoalValue());
        Optional<GoalType> goalType = getGoalType(request);
        goal.setGoalType(goalType.get());
        setAccountIfNotNull(request, goal);
        setCategoryIfNotNull(request, goal);
        return repository.update(goal);
    }

    private void setAccountIfNotNull(SingleGoalDto request, Goal goal) {
        if (request.getAccountId() != null) {
            Account account = accountService.getSingle(request.getAccountId());
            goal.setAccount(account);
        }
    }

    private void setCategoryIfNotNull(SingleGoalDto request, Goal goal) {
        if (request.getCategoryId() != null) {
            Category category = categoryService.getSingle(request.getCategoryId());
            goal.setCategory(category);
        }
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
        if (request.getGoalValue() == null) {
            throw new BadRequestException("BAD_GOAL_VALUE", "empty",
                    "Goal value cannot be empty", "Goal value is empty");
        }
        if (request.getGoalValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("BAD_GOAL_VALUE", "negative",
                    "Goal value cannot be negative", "Goal value is negative");
        }
        if (request.getCategoryId() == null && request.getAccountId() == null) {
            throw new BadRequestException("BAD_GOAL_REFERENCES", "empty", "Goal should have a category or account",
                    "No account or category selected");
        }
    }

    public void delete(Long id) {
        Goal goal = getSingle(id);
        checkIfCanAccessResource(goal.getUser());
        repository.delete(goal);
    }

    public List<GoalType> getGoalTypes() {
        return goalTypeRepository.findAll();
    }
}
