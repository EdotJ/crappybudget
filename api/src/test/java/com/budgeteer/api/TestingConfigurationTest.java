package com.budgeteer.api;

import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class TestingConfigurationTest {

    @Value("${datasources.default.url}")
    private String datasourceUrl;

    @Value("${micronaut.security.enabled}")
    private boolean isSecurityEnabled;

    @Test
    public void testConfigurationValues() {
        String[] strings = datasourceUrl.split(":");
        assertTrue(strings.length > 2);
        assertEquals("mem", strings[2]);
        assertTrue(isSecurityEnabled);
    }
}
