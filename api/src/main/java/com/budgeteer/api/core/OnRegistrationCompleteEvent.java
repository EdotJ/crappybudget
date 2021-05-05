package com.budgeteer.api.core;

import com.budgeteer.api.model.User;
import io.micronaut.context.event.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private String appUrl;
    private String clientHost;
    private User user;

    public OnRegistrationCompleteEvent(Object source) {
        super(source);
        this.appUrl = "";
        this.user = null;
        this.clientHost = "";
    }

    public OnRegistrationCompleteEvent(User user, String appUrl, String clientHost) {
        super(user);
        this.appUrl = appUrl;
        this.user = user;
        this.clientHost = clientHost;
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

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }
}
