package com.budgeteer.api.core;

import com.budgeteer.api.model.GoalType;
import com.budgeteer.api.repository.GoalTypeRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.ApplicationStartupEvent;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(notEnv = "test")
public class ApplicationStartupListener implements ApplicationEventListener<ApplicationStartupEvent> {

    private final GoalTypeRepository goalTypeRepository;

    public ApplicationStartupListener(GoalTypeRepository goalTypeRepository) {
        this.goalTypeRepository = goalTypeRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationStartupEvent event) {
        insertIfNotExists(1L, "SAVE");
        insertIfNotExists(2L, "SPEND");
    }

    private void insertIfNotExists(Long id, String goalType) {
        Optional<GoalType> goalTypeOptional = goalTypeRepository.findById(id);
        if (goalTypeOptional.isEmpty()) {
            GoalType newGoalType = new GoalType();
            newGoalType.setId(id);
            newGoalType.setName(goalType);
            goalTypeRepository.save(newGoalType);
        }
    }

    @Override
    public boolean supports(ApplicationStartupEvent event) {
        return true;
    }
}
