package com.budgeteer.api.repository;

import com.budgeteer.api.model.User;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@Repository
public abstract class UserRepository implements CrudRepository<User, Long> {

    @NonNull
    public abstract List<User> findAll();

    public abstract Optional<User> findByEmail(String email);

    public abstract Optional<User> findByUsername(String username);
}
