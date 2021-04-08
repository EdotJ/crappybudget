package com.budgeteer.api.repository;

import com.budgeteer.api.model.Entry;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends PageableRepository<Entry, Long> {

    @Join(value = "account", type = Join.Type.FETCH)
    @Join(value = "user", type = Join.Type.FETCH)
    List<Entry> findByUserIdOrderByDateDesc(Long id);

    @Join(value = "account", type = Join.Type.FETCH)
    @Join(value = "user", type = Join.Type.FETCH)
    Page<Entry> findByUserIdOrderByDateDesc(Long id, @Nullable Pageable pageable);

    @Join(value = "account", type = Join.Type.FETCH)
    @Join(value = "user", type = Join.Type.FETCH)
    Page<Entry> findByAccountIdOrderByDateDesc(Long accountId, @Nullable Pageable pageable);

    Page<Entry> findByAccountIdAndDateBetweenOrderByDateDesc(Long accountId,
                                                             LocalDate start,
                                                             LocalDate end,
                                                             @Nullable Pageable pageable);

    @Query("select sum(case when e.isExpense = true then (e.value * -1) else e.value end)  from Entry e "
            + "where e.user.id = :userId")
    Optional<BigDecimal> findSumValueByUserId(Long userId);
}
