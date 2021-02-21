package com.budgeteer.api.repository;

import com.budgeteer.api.model.Category;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public abstract class CategoryRepository implements CrudRepository<Category, Long> {

    @NonNull
    public abstract List<Category> findAll();

    @NonNull
    public abstract List<Category> findByUserId(Long id);
}
