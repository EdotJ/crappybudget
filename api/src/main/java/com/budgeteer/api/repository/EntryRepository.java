package com.budgeteer.api.repository;

import com.budgeteer.api.model.Entry;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public abstract class EntryRepository implements CrudRepository<Entry, Long> {

    @NonNull
    public abstract List<Entry> findAll();

    @NonNull
    public abstract List<Entry> findByUserId(Long id);

    public abstract List<Entry> findByAccountId(Long accountId);
}
