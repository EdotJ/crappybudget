package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.goal.GoalListDto;
import com.budgeteer.api.dto.goal.SingleGoalDto;
import com.budgeteer.api.model.Goal;
import com.budgeteer.api.service.GoalService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.authentication.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@Controller("${api.base-path}/goals")
@Produces
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @Get
    public HttpResponse<GoalListDto> getAll(Authentication principal) {
        Long userId = (Long) principal.getAttributes().get("id");
        List<SingleGoalDto> goalList = service.getAll(userId).stream()
                .map(SingleGoalDto::new)
                .collect(Collectors.toList());
        return HttpResponse.ok(new GoalListDto(goalList));
    }

    @Get("/{id}")
    public HttpResponse<SingleGoalDto> getSingle(Long id) {
        Goal goal = service.getSingle(id);
        return HttpResponse.ok(new SingleGoalDto(goal));
    }

    @Post
    public HttpResponse<SingleGoalDto> create(SingleGoalDto request, Authentication principal) {
        Goal goal = service.create(request, principal);
        return HttpResponse.created(new SingleGoalDto(goal));
    }

    @Put("/{id}")
    public HttpResponse<SingleGoalDto> update(Long id, SingleGoalDto request, Authentication principal) {
        Goal goal = service.update(id, request, principal);
        return HttpResponse.ok(new SingleGoalDto(goal));
    }

    @Delete("/{id}")
    public HttpResponse<SingleGoalDto> delete(Long id) {
        service.delete(id);
        return HttpResponse.noContent();
    }
}
