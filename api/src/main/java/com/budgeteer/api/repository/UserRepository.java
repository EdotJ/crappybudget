package com.budgeteer.api.repository;

import com.budgeteer.api.model.User;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public abstract class UserRepository implements CrudRepository<User, Long> {
}
