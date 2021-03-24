package com.budgeteer.api.repository;

import com.budgeteer.api.model.Account;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    @NonNull
    List<Account> findAll();

    List<Account> findByUserId(Long userId);

    @Query("select new Account(a, (select sum(case when e.isExpense = true then (e.value * -1) else e.value end) from Entry e where e.account.id = a.id)) from Account a where a.user.id = :userId")
    List<Account>findByUserIdWithBalance(Long userId);
}
