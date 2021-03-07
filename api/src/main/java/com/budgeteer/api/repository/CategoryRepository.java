package com.budgeteer.api.repository;

import com.budgeteer.api.model.Category;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    @NonNull
    List<Category> findAll();

    @NonNull
    List<Category> findByUserId(Long id);
}
