package com.budgeteer.api.imports;

public interface Importer<RQ, DT, DTRES> {

    void validateRequest(RQ request);

    DTRES getData(DT data) throws Exception;
}
