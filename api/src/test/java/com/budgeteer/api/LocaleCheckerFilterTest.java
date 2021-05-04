package com.budgeteer.api;

import com.budgeteer.api.core.LocaleCheckerFilter;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.convert.DefaultConversionService;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.simple.SimpleHttpHeaders;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@MicronautTest
@ExtendWith(MockitoExtension.class)
@Property(name = "flyway.enabled", value = "false")
@Tag("Unit")
public class LocaleCheckerFilterTest {

    @Inject
    LocaleCheckerFilter checkerFilter;

    @Mock
    HttpRequest<Object> mockRequest;

    @Mock
    ServerFilterChain chain;

    @Test
    public void testLocaleChangeWhenHeaderApplied() {
        HttpHeaders headers = new SimpleHttpHeaders(Map.of("Accept-Language", "LT"), DefaultConversionService.SHARED);
        when(mockRequest.getHeaders()).thenReturn(headers);
        checkerFilter.doFilter(mockRequest, chain);
        assertEquals("lt", Locale.getDefault().getLanguage());
    }

    @Test
    public void testLocaleChangeWhenNoHeaderApplied() {
        HttpHeaders headers = new SimpleHttpHeaders(Map.of(), DefaultConversionService.SHARED);
        when(mockRequest.getHeaders()).thenReturn(headers);
        checkerFilter.doFilter(mockRequest, chain);
        assertEquals("en", Locale.getDefault().getLanguage());
    }
}
