package com.budgeteer.api.repository;

import com.budgeteer.api.model.Account;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public abstract class AccountRepository implements CrudRepository<Account, Long> {

    @NonNull
    public abstract List<Account> findAll();

    public abstract List<Account> findByUserId(Long userId);
}
