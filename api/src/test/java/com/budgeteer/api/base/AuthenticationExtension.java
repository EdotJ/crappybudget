package com.budgeteer.api.base;

import com.budgeteer.api.dto.ErrorResponse;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;

@MicronautTest
public class AuthenticationExtension extends InjectableExtension
        implements BeforeTestExecutionCallback {

    private String token;

    @Inject
    @Client(value = "${api.base-path}", errorType = ErrorResponse.class)
    HttpClient httpClient;

    @Override
    public void beforeTestExecution(ExtensionContext context) throws MalformedURLException {
        initHttpClient(context);
        makeAuthRequest();
    }

    private void makeAuthRequest() {
        AuthRequest request = new AuthRequest();
        request.setUsername(TestUtils.TEST_USER_USERNAME);
        request.setPassword(TestUtils.TEST_USER_PASSWORD);
        HttpResponse<AuthResponse> response = httpClient.toBlocking().exchange(HttpRequest.POST("/api/login", request), AuthResponse.class);
        if (response.getStatus().equals(HttpStatus.OK) && response.getBody().isPresent()) {
            token = response.getBody().get().getAccessToken();
        } else {
            throw new RuntimeException("Was unable to make authentication request");
        }
    }

    private void initHttpClient(ExtensionContext context) {
        Optional<ApplicationContext> applicationContext = getMicronautApplicationContext(context);
        if (applicationContext.isPresent()) {
            EmbeddedServer embeddedServer = applicationContext.get().getBean(EmbeddedServer.class);
            httpClient = HttpClient.create(embeddedServer.getURL());
        } else {
            String testName = context.getTestClass().get().getName();
            throw new RuntimeException("Micronaut context is not available for test: " + testName);
        }
    }

    public String getToken() {
        return token;
    }

    public Map<CharSequence, CharSequence> getAuthHeader() {
        return Map.of("Authorization", "Bearer " + token);
    }
}
