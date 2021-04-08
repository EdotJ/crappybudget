package com.budgeteer.api.imports;

import com.budgeteer.api.imports.csv.ImportResult;
import com.budgeteer.api.model.Account;

import java.text.ParseException;
import java.util.List;

public interface Importer<RQ, DT extends ImporterData> {

    void validateRequest(RQ request);

    ImportResult getData(DT data) throws ParseException;

    void parse(List<ImportEntry> importEntry, Account account, boolean shouldAddUnknownCategories);
}
