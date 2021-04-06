package com.budgeteer.api;

import com.budgeteer.api.core.ProjectEnvironment;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.netty.util.ResourceLeakDetector;

public class Application {

    public static void main(String[] args) {
        ApplicationContext.builder()
                .deduceEnvironment(false)
                .defaultEnvironments(ProjectEnvironment.PRODUCTION)
                .start();
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        Micronaut.run(Application.class, args);
    }
}
