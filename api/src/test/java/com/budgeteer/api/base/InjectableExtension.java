package com.budgeteer.api.base;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.MicronautJunit5Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public abstract class InjectableExtension {

    protected static Optional<ApplicationContext> getMicronautApplicationContext(ExtensionContext extensionContext) {
        ExtensionContext.Store store = extensionContext.getRoot().getStore(ExtensionContext.Namespace.create(MicronautJunit5Extension.class));
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

}
