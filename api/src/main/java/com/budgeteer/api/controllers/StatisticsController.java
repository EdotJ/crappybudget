package com.budgeteer.api.controllers;

import com.budgeteer.api.core.Pair;
import com.budgeteer.api.dto.statistics.CategoryBreakdown;
import com.budgeteer.api.dto.statistics.TopExpenses;
import com.budgeteer.api.dto.statistics.YearlyBreakdown;
import com.budgeteer.api.service.StatisticsService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import javax.annotation.Nullable;
import java.time.LocalDate;

@Controller("${api.base-path}/statistics")
public class StatisticsController {

    private StatisticsService service;

    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @Get(value = "/category-breakdown")
    public HttpResponse<CategoryBreakdown> getCategoryBreakdown(@Nullable @QueryValue(value = "year") Integer year,
                                                                @Nullable @QueryValue(value = "month") Integer month) {
        Pair<Integer, Integer> date = getDate(year, month);
        CategoryBreakdown breakdown = service.getCategoryBreakdown(date.getFirst(), date.getSecond());
        return HttpResponse.ok(breakdown);
    }

    @Get(value = "/top-expenses")
    public HttpResponse<TopExpenses> getTopExpenses(@Nullable @QueryValue(value = "year") Integer year,
                                                    @Nullable @QueryValue(value = "month") Integer month,
                                                    @Nullable @QueryValue(value = "count") Integer count) {
        Pair<Integer, Integer> date = getDate(year, month);
        TopExpenses breakdown = service.getTopExpenses(date.getFirst(), date.getSecond(), count == null ? 5 : count);
        return HttpResponse.ok(breakdown);
    }

    @Get(value = "/yearly-breakdown")
    public HttpResponse<YearlyBreakdown> getYearlyBreakdown(@Nullable @QueryValue(value = "year") Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        YearlyBreakdown yearlyBreakdown = service.getYearlyBreakdown(year);
        return HttpResponse.ok(yearlyBreakdown);
    }

    private Pair<Integer, Integer> getDate(Integer year, Integer month) {
        LocalDate today = LocalDate.now();
        if (year == null) {
            year = today.getYear();
        }
        if (month == null) {
            month = today.getMonthValue();
        }
        return new Pair<>(year, month);
    }
}
