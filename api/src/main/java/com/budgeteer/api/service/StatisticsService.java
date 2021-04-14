package com.budgeteer.api.service;

import com.budgeteer.api.core.Service;
import com.budgeteer.api.dto.statistics.CategoryBreakdown;
import com.budgeteer.api.dto.statistics.TopExpenses;
import com.budgeteer.api.dto.statistics.YearlyBreakdown;
import com.budgeteer.api.repository.StatisticsRepository;
import com.budgeteer.api.security.RestrictedResourceHandler;
import io.micronaut.security.utils.SecurityService;

@Service
public class StatisticsService extends RestrictedResourceHandler {

    StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository repository, SecurityService securityService) {
        super(securityService);
        this.statisticsRepository = repository;
    }

    public CategoryBreakdown getCategoryBreakdown(Integer year, Integer month) {
        return statisticsRepository.getCategoryBreakdown(year, month, getAuthenticatedUserId());
    }

    public TopExpenses getTopExpenses(Integer year, Integer month, Integer count) {
        return statisticsRepository.getTopExpenses(year, month, count, getAuthenticatedUserId());
    }

    public YearlyBreakdown getYearlyBreakdown(Integer year) {
        return statisticsRepository.getYearlyBreakdown(year, getAuthenticatedUserId());
    }
}
