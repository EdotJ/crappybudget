package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.receipts.ReceiptParseResponse;
import com.budgeteer.api.receipts.OnlineReceiptParser;
import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

@Controller("${api.base-path}/receipts")
public class ReceiptHandlingController {

    private final Logger logger = LoggerFactory.getLogger(ReceiptHandlingController.class);

    private final OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> basicParser;

    private final OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> advancedParser;

    public ReceiptHandlingController(
            @Named("BasicCloudVision") OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> basic,
            @Named("AdvancedCloudVision")
                    OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> advanced) {
        this.basicParser = basic;
        this.advancedParser = advanced;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<Object> parseReceipt(CompletedFileUpload file,
                                             @QueryValue(value = "isBasic", defaultValue = "true") boolean isBasic)
            throws IOException, ParseException {
        if (logger.isDebugEnabled()) {
            logger.debug("Got receipt parsing request");
        }
        InputStream is = file.getInputStream();
        OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> receiptParser = isBasic
                ? basicParser
                : advancedParser;
        ApiResponse obj = receiptParser.makeRequest(is);
        ReceiptParseResponse itemList = receiptParser.parseReceipt(obj);
        return HttpResponse.ok().body(itemList);
    }
}
