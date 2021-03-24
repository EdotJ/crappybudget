package com.budgeteer.api;

import com.budgeteer.api.core.ProjectEnvironment;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        ApplicationContext.builder()
                .deduceEnvironment(false)
                .defaultEnvironments(ProjectEnvironment.PRODUCTION)
                .start();
        Micronaut.run(Application.class, args);
    }
}
