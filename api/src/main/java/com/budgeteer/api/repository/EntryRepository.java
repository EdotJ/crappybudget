package com.budgeteer.api.repository;

import com.budgeteer.api.model.Entry;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface EntryRepository extends CrudRepository<Entry, Long> {

    @NonNull
    List<Entry> findAll();

    @Join(value = "account", type = Join.Type.FETCH)
    @Join(value = "user", type = Join.Type.FETCH)
    @Join(value = "category", type = Join.Type.FETCH)
    List<Entry> findByUserId(Long id);

    @Join(value = "account", type = Join.Type.FETCH)
    @Join(value = "user", type = Join.Type.FETCH)
    @Join(value = "category", type = Join.Type.FETCH)
    List<Entry> findByAccountId(Long accountId);
}
