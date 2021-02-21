package com.budgeteer.api.repository;

import com.budgeteer.api.model.Goal;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public abstract class GoalRepository implements CrudRepository<Goal, Long> {

    @NonNull
    public abstract List<Goal> findAll();

    @NonNull
    public abstract List<Goal> findByUserId(Long id);
}
