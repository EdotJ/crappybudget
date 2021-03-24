package com.budgeteer.api.dto.entry;

import com.budgeteer.api.dto.AbstractListDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EntryListDto extends AbstractListDto<SingleEntryDto> {

    private int totalPages;

    public EntryListDto() {
    }

    public EntryListDto(List<SingleEntryDto> entries, int totalPages) {
        super(entries);
        this.totalPages = totalPages;
    }

    @JsonProperty("entries")
    @Override
    protected List<SingleEntryDto> getList() {
        return list;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
