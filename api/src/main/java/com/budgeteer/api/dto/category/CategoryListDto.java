package com.budgeteer.api.dto.category;

import com.budgeteer.api.dto.AbstractListDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CategoryListDto extends AbstractListDto<SingleCategoryDto> {

    public CategoryListDto() {
    }

    public CategoryListDto(List<SingleCategoryDto> accounts) {
        super(accounts);
    }

    @JsonProperty("categories")
    @Override
    protected List<SingleCategoryDto> getList() {
        return list;
    }
}
