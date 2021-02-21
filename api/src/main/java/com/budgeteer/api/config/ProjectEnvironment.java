package com.budgeteer.api.config;

import io.micronaut.context.env.Environment;

public interface ProjectEnvironment extends Environment {
    String PRODUCTION = "prod";
}
