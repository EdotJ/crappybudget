package com.budgeteer.api.base;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Optional;

@MicronautTest
public class DatabaseCleanupExtension extends InjectableExtension
        implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Inject
    private EntityManager entityManager;

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        initializeEntityManager(context);

    }

    private void initializeEntityManager(ExtensionContext context) {
        Optional<ApplicationContext> applicationContext = getMicronautApplicationContext(context);
        if (applicationContext.isPresent()) {
            entityManager = applicationContext.get().getBean(EntityManager.class);
        } else {
            String testName = context.getTestClass().get().getName();
            throw new RuntimeException("Micronaut context is not available for test: " + testName);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        String sqlString = "CREATE ALIAS IF NOT EXISTS truncate_tables FOR "
                + "\"com.budgeteer.api.base.H2Functions.truncateAllTables\"; CALL truncate_tables();";
        if (entityManager != null) {
            entityManager.createNativeQuery(sqlString).executeUpdate();
        } else {
            throw new RuntimeException("EntityManager was not initialized from application context");
        }
    }
}
