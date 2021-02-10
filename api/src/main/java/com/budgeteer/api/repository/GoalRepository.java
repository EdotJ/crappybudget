package com.budgeteer.api.repository;

import com.budgeteer.api.model.Goal;
import io.micronaut.data.repository.CrudRepository;

public abstract class GoalRepository implements CrudRepository<Goal, Long> {
}
