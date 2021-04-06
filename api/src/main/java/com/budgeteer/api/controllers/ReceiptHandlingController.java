package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.receipts.ReceiptParseResponse;
import com.budgeteer.api.exception.ServiceDisabledException;
import com.budgeteer.api.receipts.OnlineReceiptParser;
import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
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
import java.util.Base64;

@Controller("${api.base-path}/receipts")
public class ReceiptHandlingController {

    private final Logger logger = LoggerFactory.getLogger(ReceiptHandlingController.class);

    private final OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> basicParser;

    private final OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> advancedParser;

    @Property( name="api.receipt-recognition.enabled")
    private Boolean isRecognitionEnabled;

    public ReceiptHandlingController(
            @Named("BasicCloudVision") OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> basic,
            @Named("AdvancedCloudVision")
                    OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> advanced) {
        this.basicParser = basic;
        this.advancedParser = advanced;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<Object> parseReceipt(CompletedFileUpload file,
                                             @QueryValue(value = "isBasic", defaultValue = "true")  boolean isBasic,
                                             @QueryValue(value = "withImage", defaultValue = "false") boolean withImage)
            throws IOException, ParseException {
        if (!isRecognitionEnabled) {
            String msg = "We were unable to process your request";
            String detail = "Receipt recognition is disabled";
            throw new ServiceDisabledException("receipts", msg, detail);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Got receipt parsing request");
        }
        InputStream is = file.getInputStream();
        OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> receiptParser = isBasic
                ? basicParser
                : advancedParser;
        ApiResponse obj = receiptParser.makeRequest(is);
        ReceiptParseResponse parseResponse = receiptParser.parseReceipt(obj);
        if (withImage) {
            parseResponse.setBase64Encoding(obj.getBase64Encoding());
        }
        return HttpResponse.ok().body(parseResponse);
    }
}
