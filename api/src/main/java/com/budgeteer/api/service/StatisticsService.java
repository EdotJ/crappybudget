package com.budgeteer.api.service;

import com.budgeteer.api.core.Service;
import com.budgeteer.api.dto.statistics.CategoryBreakdown;
import com.budgeteer.api.dto.statistics.TopExpenses;
import com.budgeteer.api.dto.statistics.YearlyBreakdown;
import com.budgeteer.api.repository.StatisticsRepository;

@Service
public class StatisticsService {

    StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository repository) {
        this.statisticsRepository = repository;
    }

    public CategoryBreakdown getCategoryBreakdown(Integer year, Integer month) {
        return statisticsRepository.getCategoryBreakdown(year, month);
    }

    public TopExpenses getTopExpenses(Integer year, Integer month, Integer count) {
        return statisticsRepository.getTopExpenses(year, month, count);
    }

    public YearlyBreakdown getYearlyBreakdown(Integer year) {
        return statisticsRepository.getYearlyBreakdown(year);
    }
}
