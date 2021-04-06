package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.receipts.gcp.model.request.*;
import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
import com.budgeteer.api.receipts.gcp.model.response.ErrorResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.jackson.annotation.JacksonFeatures;
import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Singleton
@JacksonFeatures(disabledDeserializationFeatures = DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
public class CloudVisionClient {

    Logger logger = LoggerFactory.getLogger(CloudVisionClient.class);

    @Inject
    ObjectMapper objectMapper;

    @Client("https://vision.googleapis.com/v1")
    final RxHttpClient client;

    final CloudVisionConfig config;

    public CloudVisionClient(RxHttpClient client, CloudVisionConfig config) {
        this.client = client;
        this.config = config;
    }

    public ApiResponse getAnnotatedImage(InputStream is) throws IOException {
        Request request = new Request();
        Image image = new Image();
        String base64Encoding = Base64.getEncoder().encodeToString(is.readAllBytes());
        image.setContent(base64Encoding);
        List<Feature> features = new ArrayList<>();
        Feature textFeature = new Feature();
        textFeature.setType(DetectionType.DOCUMENT_TEXT_DETECTION);
        features.add(textFeature);
        ImageRequest imageRequest = new ImageRequest(image, features);
        request.getImageRequests().add(imageRequest);
        String uri = "https://vision.googleapis.com/v1/images:annotate?key=" + config.getKey();
        Maybe<ApiResponse> response = client.retrieve(
                HttpRequest.POST(uri, request),
                Argument.of(ApiResponse.class),
                Argument.of(ErrorResponse.class)).firstElement();
        try {
            ApiResponse finalResponse = response.blockingGet();
            finalResponse.setBase64Encoding(base64Encoding);
            if (logger.isDebugEnabled()) {
                logger.debug(objectMapper.writeValueAsString(finalResponse));
            }
            return finalResponse;
        } catch (HttpClientResponseException e) {
            var error = e.getResponse().getBody(ErrorResponse.class);
            if (error.isPresent()) {
                logger.error(error.get().getError().getMessage());
            } else {
                logger.error("Failed POSTing to GCP Cloud Vision");
            }
            throw e;
        }
    }
}
