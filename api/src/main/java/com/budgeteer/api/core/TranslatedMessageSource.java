package com.budgeteer.api.core;

import io.micronaut.context.MessageSource;
import io.micronaut.context.i18n.ResourceBundleMessageSource;

import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.Locale;

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

    public String getMessageWithDefaultLocale(String property, String defaultMessage, Object... args) {
        MessageSource.MessageContext messageContext = MessageSource.MessageContext.of(Locale.getDefault());
        String localized = messageSource.getRawMessage(property, messageContext, defaultMessage);
        return MessageFormat.format(localized, args);
    }
}
