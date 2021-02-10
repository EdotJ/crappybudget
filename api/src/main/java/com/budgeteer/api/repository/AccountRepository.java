package com.budgeteer.api.repository;

import com.budgeteer.api.model.Account;
import io.micronaut.data.repository.CrudRepository;

public abstract class AccountRepository implements CrudRepository<Account, Long> {
}
