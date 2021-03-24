package com.budgeteer.api.core;

import com.budgeteer.api.model.User;
import io.micronaut.context.event.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private String appUrl;
    private User user;

    public OnRegistrationCompleteEvent(Object source) {
        super(source);
        this.appUrl = "";
        this.user = null;
    }

    public OnRegistrationCompleteEvent(User user, String appUrl) {
        super(user);
        this.appUrl = appUrl;
        this.user = user;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
