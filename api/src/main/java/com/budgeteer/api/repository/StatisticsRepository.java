package com.budgeteer.api.repository;

import com.budgeteer.api.dto.statistics.CategoryBreakdown;
import com.budgeteer.api.dto.statistics.TopExpenses;
import com.budgeteer.api.dto.statistics.YearlyBreakdown;
import io.micronaut.data.annotation.Repository;
import io.micronaut.transaction.annotation.TransactionalAdvice;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Repository
public abstract class StatisticsRepository {

    private final EntityManager entityManager;

    public StatisticsRepository(EntityManager em) {
        this.entityManager = em;
    }

    @TransactionalAdvice
    public CategoryBreakdown getCategoryBreakdown(Integer year, Integer month) {
        Query query = entityManager.createQuery("select "
                + "c.parent.id, c.id, "
                + "(SELECT COALESCE(SUM(value), 0) FROM Entry e "
                + "WHERE e.category = c AND e.isExpense = true AND YEAR(e.date) = :year AND MONTH(e.date) = :month) "
                + "FROM Category c "
                + "GROUP BY c.id");
        query.setParameter("year", year);
        query.setParameter("month", month);

        @SuppressWarnings("unchecked")
        List<CategoryBreakdown.Entry> entries = (List<CategoryBreakdown.Entry>)
                query.unwrap(org.hibernate.query.Query.class)
                        .setResultTransformer(new ResultTransformer() {
                            @Override
                            public Object transformTuple(Object[] tuple, String[] aliases) {
                                return new CategoryBreakdown.Entry(
                                        tuple[0] == null ? null : ((Number) tuple[0]).longValue(),
                                        ((Number) tuple[1]).longValue(),
                                        ((BigDecimal) tuple[2]).setScale(2, RoundingMode.CEILING));
                            }

                            @Override
                            public List transformList(List collection) {
                                return collection;
                            }
                        })
                        .getResultList();

        CategoryBreakdown breakdown = new CategoryBreakdown();
        breakdown.setCategories(entries);

        return breakdown;
    }

    @TransactionalAdvice
    public TopExpenses getTopExpenses(Integer year, Integer month, Integer count) {
        Query query = entityManager.createQuery("select "
                + "e.name, e.account.name, e.value, e.date "
                + "FROM Entry e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month AND e.isExpense = true "
                + "ORDER BY e.value DESC");
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setMaxResults(count);

        @SuppressWarnings("unchecked")
        List<TopExpenses.Entry> entries = (List<TopExpenses.Entry>)
                query.unwrap(org.hibernate.query.Query.class)
                        .setResultTransformer(new ResultTransformer() {
                            @Override
                            public Object transformTuple(Object[] tuple, String[] aliases) {
                                return new TopExpenses.Entry(
                                        (String) tuple[0],
                                        (String) tuple[1],
                                        ((BigDecimal) tuple[2]).setScale(2, RoundingMode.CEILING),
                                        (LocalDate) tuple[3]);
                            }

                            @Override
                            public List transformList(List collection) {
                                return collection;
                            }
                        })
                        .getResultList();

        return new TopExpenses(entries);
    }

    @TransactionalAdvice
    public YearlyBreakdown getYearlyBreakdown(Integer year) {
        Query query = entityManager.createQuery(
                "select month(e.date), "
                        + "coalesce(sum(case when e.isExpense = false then e.value else 0 end), 0) as income, "
                        + "coalesce(sum(case when e.isExpense = true then e.value else 0 end), 0) as expenses "
                        + "FROM Entry e WHERE YEAR(e.date) = :year GROUP BY month(e.date)");
        query.setParameter("year", year);

        @SuppressWarnings("unchecked")
        List<YearlyBreakdown.Entry> entries = (List<YearlyBreakdown.Entry>)
                query.unwrap(org.hibernate.query.Query.class)
                        .setResultTransformer(new ResultTransformer() {
                            @Override
                            public Object transformTuple(Object[] tuple, String[] aliases) {
                                return new YearlyBreakdown.Entry(
                                        ((Number) tuple[0]).intValue(),
                                        ((BigDecimal) tuple[1]).setScale(2, RoundingMode.CEILING),
                                        ((BigDecimal) tuple[2]).setScale(2, RoundingMode.CEILING));
                            }

                            @Override
                            public List transformList(List collection) {
                                return collection;
                            }
                        })
                        .getResultList();

        return new YearlyBreakdown(entries);
    }
}
