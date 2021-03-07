package com.budgeteer.api.repository;

import com.budgeteer.api.model.Goal;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface GoalRepository extends CrudRepository<Goal, Long> {

    @NonNull
    List<Goal> findAll();

    @NonNull
    List<Goal> findByUserId(Long id);
}
