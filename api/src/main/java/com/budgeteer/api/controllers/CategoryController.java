package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.category.CategoryListDto;
import com.budgeteer.api.dto.category.SingleCategoryDto;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.service.CategoryService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Controller("${api.base-path}/categories")
public class CategoryController {

    @Inject
    private final CategoryService service;

    public CategoryController(CategoryService categoryService) {
        this.service = categoryService;
    }

    @Get
    public HttpResponse<CategoryListDto> getAll(@QueryValue Long userId) {
        List<SingleCategoryDto> singleCategoryList = service.getAll(userId).stream()
                .map(SingleCategoryDto::new)
                .collect(Collectors.toList());
        return HttpResponse.ok(new CategoryListDto(singleCategoryList));
    }

    @Get("/{id}")
    public HttpResponse<SingleCategoryDto> getSingle(Long id) {
        Category category = service.getSingle(id);
        return HttpResponse.ok(new SingleCategoryDto(category));
    }

    @Post
    public HttpResponse<SingleCategoryDto> create(SingleCategoryDto request) {
        Category category = service.create(request);
        return HttpResponse.created(new SingleCategoryDto(category));
    }

    @Put("/{id}")
    public HttpResponse<SingleCategoryDto> update(Long id, SingleCategoryDto request) {
        Category updatedCategory = service.update(id, request);
        return HttpResponse.ok(new SingleCategoryDto(updatedCategory));
    }

    @Delete("/{id}")
    public HttpResponse<SingleCategoryDto> delete(Long id) {
        service.delete(id);
        return HttpResponse.ok();
    }
}
