package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.entry.EntryListDto;
import com.budgeteer.api.dto.entry.SingleEntryDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.service.EntryService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.authentication.Authentication;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@Controller("${api.base-path}/entries")
@Produces
public class EntryController {

    private final EntryService service;

    public EntryController(EntryService entryService) {
        this.service = entryService;
    }

    @Get("{?accountId}")
    public HttpResponse<EntryListDto> getAll(@QueryValue @Nullable Long accountId, Authentication principal) {
        if (accountId == null && principal.getAttributes().get("id") == null) {
            String msg = "No user or account provided for entry";
            String detail = "Account identifier or user identifier is missing";
            throw new BadRequestException("PARAM_MISSING", "entries", msg, detail);
        }
        Long userId = (Long) principal.getAttributes().get("id");
        List<Entry> entryList = accountId != null ? service.getAllByAccount(accountId) : service.getAllByUser(userId);
        List<SingleEntryDto> singleEntries = entryList.stream().map(SingleEntryDto::new).collect(Collectors.toList());
        return HttpResponse.ok(new EntryListDto(singleEntries));
    }

    @Get("/{id}")
    public HttpResponse<SingleEntryDto> getSingle(Long id) {
        Entry entry = service.getSingle(id);
        return HttpResponse.ok(new SingleEntryDto(entry));
    }

    @Post
    public HttpResponse<SingleEntryDto> create(SingleEntryDto request) {
        Entry entry = service.create(request);
        return HttpResponse.created(new SingleEntryDto(entry));
    }

    @Put("/{id}")
    public HttpResponse<SingleEntryDto> update(Long id, SingleEntryDto request, Authentication principal) {
        Entry entry = service.update(id, request, principal);
        return HttpResponse.ok(new SingleEntryDto(entry));
    }

    @Delete("/{id}")
    public HttpResponse<SingleEntryDto> delete(Long id) {
        service.delete(id);
        return HttpResponse.noContent();
    }
}
