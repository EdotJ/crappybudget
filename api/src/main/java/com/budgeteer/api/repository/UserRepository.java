package com.budgeteer.api.repository;

import com.budgeteer.api.model.User;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public abstract class UserRepository implements CrudRepository<User, Long> {

    @NonNull
    public abstract List<User> findAll();
}
