package com.budgeteer.api.controllers;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;

import javax.inject.Inject;
import java.util.Optional;

@Controller
public class ClientForwardingController {

    @Value("${api.base-path}")
    private String baseApiPath;

    @Inject
    ResourceResolver resourceResolver;

    @Get(value = "/{path:^((?!${api.base-path}\\/).).*(?<!\\..+)$", produces = MediaType.TEXT_PLAIN)
    Optional<StreamedFile> forward(HttpRequest<?> request) {
        // TODO: fix for 404s that are responses from the API
        // Check if request was to the APi or not
        return resourceResolver.getResource("classpath:public/index.html")
                .map(StreamedFile::new);
    }
}
