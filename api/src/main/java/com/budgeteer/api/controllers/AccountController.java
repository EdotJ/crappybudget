package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.account.AccountListDto;
import com.budgeteer.api.dto.account.SingleAccountDto;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.service.AccountService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller("${api.base-path:/api}/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService service) {
        this.accountService = service;
    }

    @Get
    @Produces
    public HttpResponse<AccountListDto> getAll(@QueryValue Long userId) {
        List<SingleAccountDto> accounts = accountService.getAll(userId).stream()
                .map(SingleAccountDto::new)
                .collect(Collectors.toList());
        return HttpResponse.ok(new AccountListDto(accounts));
    }

    @Get(value = "/{id}")
    @Produces
    public HttpResponse<SingleAccountDto> getSingle(Long id) {
        Account account = accountService.getSingle(id);
        return HttpResponse.ok(new SingleAccountDto(account));
    }

    @Post
    @Produces
    public HttpResponse<SingleAccountDto> create(@Body SingleAccountDto request) {
        Account account = accountService.create(request);
        return HttpResponse.created(new SingleAccountDto(account));
    }

    @Put(value = "{id}")
    @Produces
    public HttpResponse<SingleAccountDto> update(Long id, @Body SingleAccountDto request) {
        Account account = accountService.update(id, request);
        return HttpResponse.ok(new SingleAccountDto(account));
    }

    @Delete(value = "/{id}")
    @Produces
    public HttpResponse<Object> delete(Long id) {
        accountService.delete(id);
        return HttpResponse.ok();
    }
}
