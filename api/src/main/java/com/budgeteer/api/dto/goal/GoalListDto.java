package com.budgeteer.api.dto.goal;

import com.budgeteer.api.dto.AbstractListDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GoalListDto extends AbstractListDto<SingleGoalDto> {

    public GoalListDto() {
    }

    public GoalListDto(List<SingleGoalDto> goals) {
        super(goals);
    }

    @JsonProperty("goals")
    @Override
    protected List<SingleGoalDto> getList() {
        return list;
    }
}
