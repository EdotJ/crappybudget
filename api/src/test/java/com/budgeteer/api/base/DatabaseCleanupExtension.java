package com.budgeteer.api.base;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.MicronautJunit5Extension;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Optional;

@MicronautTest
public class DatabaseCleanupExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

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

    private static Optional<ApplicationContext> getMicronautApplicationContext(ExtensionContext extensionContext) {
        Store store = extensionContext.getRoot().getStore(Namespace.create(MicronautJunit5Extension.class));
        if (store != null) {
            try {
                ApplicationContext appContext = (ApplicationContext) store.get(ApplicationContext.class);
                if (appContext != null) {
                    return Optional.of(appContext);
                }
            } catch (ClassCastException ignored) {

            }
        }
        return Optional.empty();
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
