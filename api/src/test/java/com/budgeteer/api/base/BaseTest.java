package com.budgeteer.api.base;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.annotation.Nonnull;
import java.util.Map;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest implements TestPropertyProvider {

    @Property(name="datasources.default.url")
    private String dataSourceUrl;

    @Test
    void testDataSourceUrlIsCorrect() {
        Assertions.assertEquals("jdbc:h2:mem:default;", dataSourceUrl);
    }

    @Nonnull
    @Override
    public Map<String, String> getProperties() {
        return CollectionUtils.mapOf(
                "datasources.default.url", "jdbc:h2:mem:default;"
        );
    }
}
