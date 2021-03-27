package com.budgeteer.api.receipts;

import java.io.IOException;

public interface OnlineReceiptParser<MODEL, REQ, RES> extends ReceiptParser<REQ, RES> {

    REQ makeRequest(MODEL receipt) throws IOException;
}
