package com.budgeteer.api.repository;

import com.budgeteer.api.model.User;
import com.budgeteer.api.model.YnabCategory;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface YnabCategoryRepository extends CrudRepository<YnabCategory, Long> {

    List<YnabCategory> findByUser(User user);
}
