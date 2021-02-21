package com.budgeteer.api.dto.entry;

import com.budgeteer.api.dto.AbstractListDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EntryListDto extends AbstractListDto<SingleEntryDto> {

    public EntryListDto() {
    }

    public EntryListDto(List<SingleEntryDto> entries) {
        super(entries);
    }

    @JsonProperty("entries")
    @Override
    protected List<SingleEntryDto> getList() {
        return list;
    }
}
