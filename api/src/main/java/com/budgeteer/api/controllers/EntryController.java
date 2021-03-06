package com.budgeteer.api.controllers;

import com.budgeteer.api.core.Pair;
import com.budgeteer.api.dto.BalanceDto;
import com.budgeteer.api.dto.account.AccountBalanceDto;
import com.budgeteer.api.dto.entry.EntryListDto;
import com.budgeteer.api.dto.entry.ReceiptEntriesDto;
import com.budgeteer.api.dto.entry.ReceiptEntryDto;
import com.budgeteer.api.dto.entry.SingleEntryDto;
import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.service.EntryService;
import io.micronaut.core.convert.format.Format;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.authentication.Authentication;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@Controller("${api.base-path}/entries")
@Produces
public class EntryController {

    private final EntryService service;

    public EntryController(EntryService entryService) {
        this.service = entryService;
    }

    @Get("{?accountId}")
    public HttpResponse<EntryListDto> getAll(@QueryValue @Nullable Long accountId,
                                             @QueryValue @Format("yyyy-MM-dd") @Nullable LocalDate from,
                                             @QueryValue @Format("yyyy-MM-dd") @Nullable LocalDate to,
                                             Pageable pageable,
                                             Authentication principal) {
        if (accountId == null && principal.getAttributes().get("id") == null) {
            String msg = "No user or account provided for entry";
            String detail = "Account identifier or user identifier is missing";
            throw new BadRequestException("PARAM_MISSING", "entries", msg, detail);
        }
        Page<Entry> entryPage = accountId != null
                ? service.getAllByAccount(accountId, from, to, pageable)
                : service.getAllByUser(from, to, pageable);
        List<SingleEntryDto> singleEntries = entryPage.getContent().stream()
                .map(SingleEntryDto::new)
                .collect(Collectors.toList());
        return HttpResponse.ok(new EntryListDto(singleEntries, entryPage.getTotalPages()));
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

    @Post("/receipt")
    public HttpResponse<EntryListDto> create(ReceiptEntriesDto request) {
        filterOutDiscounts(request);
        List<Entry> entryList = service.createAll(request);
        List<SingleEntryDto> entries = entryList.stream()
                .map(SingleEntryDto::new)
                .collect(Collectors.toList());
        return HttpResponse.created(new EntryListDto(entries));
    }

    private void filterOutDiscounts(ReceiptEntriesDto request) {
        ListIterator<ReceiptEntryDto> entryIterator = request.getEntries().listIterator();
        while (entryIterator.hasNext()) {
            ReceiptEntryDto entry = entryIterator.next();
            if (entry.isDiscount()) {
                ReceiptEntryDto nonDiscountEntry = request.getEntries().get(entryIterator.previousIndex() - 1);
                nonDiscountEntry.setPrice(nonDiscountEntry.getPrice().subtract(entry.getPrice().abs()));
                entryIterator.remove();
            }
        }
    }

    @Put("/{id}")
    public HttpResponse<SingleEntryDto> update(Long id, SingleEntryDto request) {
        Entry entry = service.update(id, request);
        return HttpResponse.ok(new SingleEntryDto(entry));
    }

    @Delete("/{id}")
    public HttpResponse<SingleEntryDto> delete(Long id) {
        service.delete(id);
        return HttpResponse.noContent();
    }

    @Get("/currentMonth")
    public HttpResponse<AccountBalanceDto> getCurrentMonthBalanceSummary(Long accountId) {
        Pair<BigDecimal, BigDecimal> pair = service.getMonthlyBalance(accountId);
        return HttpResponse.ok(new AccountBalanceDto(pair.getSecond(), pair.getFirst()));
    }

    @Get("/balance")
    public HttpResponse<BalanceDto> getTotalBalance() {
        return HttpResponse.ok(new BalanceDto(service.getBalance()));
    }
}
