package com.budgeteer.api.controllers;

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

    @Inject
    ResourceResolver resourceResolver;

    @Get(value = "/{path:^((?!${api.base-path}\\/).).*(?<!\\..+)$", produces = MediaType.TEXT_PLAIN)
    Optional<StreamedFile> forward(HttpRequest<?> request) {
        return resourceResolver.getResource("classpath:public/index.html").map(StreamedFile::new);
    }
}
