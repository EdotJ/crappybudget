package com.budgeteer.api.controllers;

import com.budgeteer.api.receipts.OnlineReceiptParser;
import com.budgeteer.api.receipts.ReceiptParser;
import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
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

    private final ReceiptParser<ApiResponse, List<Object>> receiptParser;

    public ReceiptHandlingController(@Named("BasicCloudVision") ReceiptParser<ApiResponse, List<Object>> receiptParser) {
        this.receiptParser = receiptParser;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<Object> parseReceipt(CompletedFileUpload file) throws IOException, ParseException {
        if (logger.isDebugEnabled()) {
            logger.debug("Got receipt parsing request");
        }
        InputStream is = file.getInputStream();
        if (receiptParser instanceof OnlineReceiptParser) {
            ApiResponse obj = ((OnlineReceiptParser<InputStream, ApiResponse, ?>) receiptParser).makeRequest(is);
            List<Object> itemList = receiptParser.parseReceipt(obj);
            return HttpResponse.ok().body(itemList);
        }
        return HttpResponse.ok();
    }
}
