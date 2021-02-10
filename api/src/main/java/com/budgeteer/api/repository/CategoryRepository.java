package com.budgeteer.api.repository;

import com.budgeteer.api.model.Category;
import io.micronaut.data.repository.CrudRepository;

public abstract class CategoryRepository implements CrudRepository<Category, Long> {
}
