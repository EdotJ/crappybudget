package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
import com.budgeteer.api.receipts.gcp.model.response.ErrorResponse;
import io.micronaut.core.type.Argument;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Flowable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
@Tag("Unit")
public class CloudVisionClientTest {

    @Inject
    CloudVisionClient client;

    @Inject
    RxHttpClient httpClient;

    @Test
    public void testImageGet() throws IOException {
        ApiResponse response = new ApiResponse();
        when(httpClient.retrieve(any(), eq(Argument.of(ApiResponse.class)), eq(Argument.of(ErrorResponse.class))))
                .thenReturn(Flowable.just(response));
        try (InputStream is = new ByteArrayInputStream("text".getBytes(StandardCharsets.UTF_8))) {
            assertNotNull(client.getImageAnnotations(is));
        }
    }

    @MockBean(RxHttpClient.class)
    RxHttpClient httpClient() {
        return mock(RxHttpClient.class);
    }
}
