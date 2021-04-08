package com.budgeteer.api.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class CategoryBreakdown {

    @JsonProperty("breakdown")
    private List<Entry> categories;

    public CategoryBreakdown() {
    }

    public CategoryBreakdown(List<Entry> categories) {
        this.categories = categories;
    }

    public List<Entry> getCategories() {
        return categories;
    }

    public void setCategories(List<Entry> categories) {
        this.categories = categories;
    }

    @JsonProperty("total")
    public BigDecimal getTotal() {
        return categories.stream().map(c -> c.value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static class Entry {
        private Long parentId;
        private Long categoryId;
        private BigDecimal value;

        public Entry() {
        }

        public Entry(Long parentId, Long categoryId, BigDecimal value) {
            this.parentId = parentId;
            this.categoryId = categoryId;
            this.value = value;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}
