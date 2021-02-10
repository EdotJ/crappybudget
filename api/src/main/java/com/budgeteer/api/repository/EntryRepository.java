package com.budgeteer.api.repository;

import com.budgeteer.api.model.Entry;
import io.micronaut.data.repository.CrudRepository;

public abstract class EntryRepository implements CrudRepository<Entry, Long> {
}
