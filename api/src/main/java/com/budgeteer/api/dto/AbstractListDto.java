package com.budgeteer.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractListDto<SINGLE> {

    protected final List<SINGLE> list;

    public AbstractListDto(Collection<SINGLE> list) {
        this();
        this.list.addAll(list);
    }

    public AbstractListDto() {
        list = new ArrayList<>();
    }

    protected List<SINGLE> getList() {
        return list;
    }

    @JsonProperty("count")
    public int getCount() {
        return list.size();
    }
}
