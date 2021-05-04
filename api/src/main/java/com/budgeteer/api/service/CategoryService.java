package com.budgeteer.api.service;

import com.budgeteer.api.core.Service;
import com.budgeteer.api.dto.category.SingleCategoryDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.CategoryRepository;
import com.budgeteer.api.security.RestrictedResourceHandler;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.utils.SecurityService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService extends RestrictedResourceHandler {

    private final UserService userService;

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository repository, UserService userService, SecurityService securityService) {
        super(securityService);
        this.categoryRepository = repository;
        this.userService = userService;
    }

    public List<Category> getAll(Long userId) {
        User user = userService.getById(userId);
        checkIfCanAccessResource(user);
        return categoryRepository.findByUserId(user.getId());
    }

    public Category getSingle(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            String defaultMsg = "This category does not exist";
            throw new ResourceNotFoundException("NOT_FOUND", "category", defaultMsg, "Category was not found");
        }
        Category category = optionalCategory.get();
        checkIfCanAccessResource(category.getUser());
        return category;
    }

    public Category create(SingleCategoryDto request) {
        validateCategoryRequest(request);

        User user = userService.getById(getAuthenticatedUserId());
        Category category = new Category();
        category.setName(request.getName());
        category.setUser(user);
        category.setBudgeted(BigDecimal.ZERO);
        if (request.getParentId() != null) {
            Category parent = findParentCategory(request.getParentId());
            checkParent(parent);
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    private void checkParent(Category parent) {
        if (parent != null && parent.getParent() != null) {
            throw new BadRequestException("BAD_CATEGORY", "category_nesting",
                    "Category's parent already has a parent", "Parent reference already in place");
        }
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Long id, SingleCategoryDto request) {
        Category category = getSingle(id);
        validateCategoryUpdateRequest(request, category);
        checkIfCanAccessResource(category.getUser());
        category.setName(request.getName());
        boolean isDiffCategory = isDiffCategory(request, category);
        if (isDiffCategory) {
            Category parent = null;
            if (request.getParentId() != null) {
                parent = findParentCategory(request.getParentId());
                checkParent(parent);
            }
            category.setParent(parent);
        }
        if (request.getBudgeted() != null) {
            category.setBudgeted(request.getBudgeted());
        }
        return categoryRepository.update(category);
    }

    private boolean isDiffCategory(SingleCategoryDto request, Category category) {
        return (category.getParent() == null && request.getParentId() != null)
                || (request.getParentId() != null
                && category.getParent() != null
                && !request.getParentId().equals(category.getParent().getId()))
                || (category.getParent() != null && request.getParentId() == null);
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
        if (!StringUtils.hasText(request.getName())) {
            throw new BadRequestException("BAD_CATEGORY_NAME", "empty",
                    "Category name cannot be empty", "Category name is empty");
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
        checkIfCanAccessResource(category.getUser());
        categoryRepository.delete(category);
    }
}
