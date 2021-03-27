package com.budgeteer.api.receipts;

import java.text.ParseException;

public interface ReceiptParser<REQ, RES> {

    RES parseReceipt(REQ request) throws ParseException;
}
