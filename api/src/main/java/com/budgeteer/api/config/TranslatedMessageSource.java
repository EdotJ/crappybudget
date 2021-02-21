package com.budgeteer.api.config;

import io.micronaut.context.MessageSource;
import io.micronaut.context.i18n.ResourceBundleMessageSource;

import javax.inject.Singleton;
import java.util.Locale;
import java.util.Optional;

@Singleton
public class TranslatedMessageSource {

    private static final String MESSAGE_BUNDLE_BASE_NAME = "messages";

    private final ResourceBundleMessageSource messageSource;

    public TranslatedMessageSource() {
        messageSource = new ResourceBundleMessageSource(MESSAGE_BUNDLE_BASE_NAME);
    }

    public ResourceBundleMessageSource getMessageSource() {
        return messageSource;
    }

    public String getMessageWithDefaultLocale(String property, String defaultMessage) {
        MessageSource.MessageContext messageContext = MessageSource.MessageContext.of(Locale.getDefault());
        Optional<String> localized = messageSource.getMessage(property, messageContext);
        return localized.orElse(defaultMessage);
    }
}
