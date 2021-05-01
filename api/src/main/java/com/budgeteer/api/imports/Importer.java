package com.budgeteer.api.imports;

import com.budgeteer.api.imports.csv.ImportResult;
import com.budgeteer.api.model.Account;

import java.text.ParseException;
import java.util.List;

public interface Importer<RQ, DT, DTRES> {

    void validateRequest(RQ request);

    DTRES getData(DT data) throws Exception;
}
