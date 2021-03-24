package com.budgeteer.api.core;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("emailprovider")
public class EmailConfig {

    private boolean enabled;

    private ServerConfig serverConfig = new ServerConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @ConfigurationProperties("server")
    public static class ServerConfig {

        private String host;

        private int port;

        private String username;

        private String password;

        private String emailAddress;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public int getPort() {
            return port;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmailAddress() {
            return emailAddress;
        }
    }
}
