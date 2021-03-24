package com.budgeteer.api.core;

import io.micronaut.context.env.Environment;

public interface ProjectEnvironment extends Environment {
    String PRODUCTION = "prod";
}
