package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.user.SingleUserDto;
import com.budgeteer.api.dto.user.UserListDto;
import com.budgeteer.api.model.User;
import com.budgeteer.api.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller("${api.base-path:/api}/users")
@Produces
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Get
    public HttpResponse<UserListDto> getAll() {
        List<SingleUserDto> users = userService.getAll().stream().map(SingleUserDto::new).collect(Collectors.toList());
        return HttpResponse.ok(new UserListDto(users));
    }

    @Get(value = "/{id}")
    public HttpResponse<SingleUserDto> getSingle(Long id) {
        return HttpResponse.ok(new SingleUserDto(userService.getById(id)));
    }

    @Post
    public HttpResponse<SingleUserDto> create(@Body SingleUserDto request) {
        User user = userService.create(request);
        return HttpResponse.created(new SingleUserDto(user));
    }

    @Put(value = "/{id}")
    public HttpResponse<SingleUserDto> update(@Body SingleUserDto request, Long id) {
        User user = userService.update(id, request);
        return HttpResponse.ok(new SingleUserDto(user));
    }

    @Delete(value = "/{id}")
    public HttpResponse<Object> delete(Long id) {
        userService.delete(id);
        return HttpResponse.noContent();
    }
}
