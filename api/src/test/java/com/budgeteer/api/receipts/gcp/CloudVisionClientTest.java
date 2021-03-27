package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.receipts.gcp.CloudVisionClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@MicronautTest
public class CloudVisionClientTest {

    @Inject
    CloudVisionClient client;

    @Test
    public void testImageGet() throws IOException {
        try (InputStream is = new ByteArrayInputStream("text".getBytes(StandardCharsets.UTF_8))) {
            Object object = client.getAnnotatedImage(is);
        }
    }
}
