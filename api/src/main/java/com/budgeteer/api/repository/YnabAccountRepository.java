package com.budgeteer.api.repository;

import com.budgeteer.api.model.User;
import com.budgeteer.api.model.YnabAccount;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface YnabAccountRepository extends CrudRepository<YnabAccount, Long> {

    List<YnabAccount> findByUser(User user);
}
