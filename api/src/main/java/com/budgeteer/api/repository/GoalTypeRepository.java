package com.budgeteer.api.repository;

import com.budgeteer.api.model.GoalType;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface GoalTypeRepository extends CrudRepository<GoalType, Long> {

    @NonNull
    List<GoalType> findAll();
}
