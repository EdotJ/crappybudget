package com.budgeteer.api.repository;

import com.budgeteer.api.model.Account;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    @NonNull
    List<Account> findAll();

    List<Account> findByUserId(Long userId);
}
