package com.budgeteer.api.service;

import com.budgeteer.api.annotation.Service;
import com.budgeteer.api.dto.category.SingleCategoryDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.CategoryRepository;
import io.micronaut.core.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

@Service
public class CategoryService {

    UserService userService;

    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository repository, UserService userService) {
        this.categoryRepository = repository;
        this.userService = userService;
    }

    public Collection<Category> getAll(Long userId) {
        User user = userService.getSingle(userId);
        return categoryRepository.findByUserId(user.getId());
    }

    public Category getSingle(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            String defaultMsg = "This category does not exist";
            throw new ResourceNotFoundException("NOT_FOUND", "category", defaultMsg, "Category was not found");
        }
        return category.get();
    }

    public Category create(SingleCategoryDto request) {
        validateCategoryRequest(request);
        User user = userService.getSingle(request.getUserId());
        Category category = new Category();
        category.setName(request.getName());
        category.setUser(user);
        category.setBudgeted(BigDecimal.ZERO);
        if (request.getParentId() != null) {
            Category parent = findParentCategory(request.getParentId());
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    public Category update(Long id, SingleCategoryDto request) {
        Category category = getSingle(id);
        validateCategoryUpdateRequest(request, category);
        // TODO: this check is not necessary after auth
        User newUser = null;
        if (!request.getUserId().equals(category.getUser().getId())) {
            newUser = userService.getSingle(request.getUserId());
        }
        category.setName(request.getName());
        if (request.getBudgeted() != null) {
            category.setBudgeted(request.getBudgeted());
        }
        if (newUser != null) {
            category.setUser(newUser);
        }
        return categoryRepository.update(category);
    }

    private Category findParentCategory(Long parentId) {
        Optional<Category> category = categoryRepository.findById(parentId);
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("NOT_FOUND", "parent_category",
                    "Parent category does not exist", "Parent category was not found");
        }
        return category.get();
    }

    private void validateCategoryRequest(SingleCategoryDto request) {
        if (StringUtils.isEmpty(request.getName()) || !StringUtils.hasText(request.getName())) {
            throw new BadRequestException("BAD_CATEGORY_NAME", "empty",
                    "Category name cannot be empty", "Category name is empty");
        }
        if (request.getUserId() == null) {
            throw new BadRequestException("BAD_CATEGORY", "no_user_id",
                    "No user identifier provided", "User id was not supplied");
        }
    }

    private void validateCategoryUpdateRequest(SingleCategoryDto request, Category category) {
        validateCategoryRequest(request);
        if (category.getId().equals(request.getParentId())) {
            throw new BadRequestException("BAD_CATEGORY", "self_reference",
                    "Category cannot be parent of itself", "Self referencing Category inputted");
        }
    }

    public void delete(Long id) {
        Category category = getSingle(id);
        categoryRepository.delete(category);
    }
}
