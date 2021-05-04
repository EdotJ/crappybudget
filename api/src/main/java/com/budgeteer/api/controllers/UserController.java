package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.user.ResendEmailRequest;
import com.budgeteer.api.dto.user.ResetPasswordRequest;
import com.budgeteer.api.dto.user.SingleUserDto;
import com.budgeteer.api.dto.user.UserListDto;
import com.budgeteer.api.exception.ServiceDisabledException;
import com.budgeteer.api.model.User;
import com.budgeteer.api.service.EmailService;
import com.budgeteer.api.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller("${api.base-path:/api}")
@Produces
public class UserController {

    private final UserService userService;

    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Get("/users")
    public HttpResponse<UserListDto> getAll() {
        List<SingleUserDto> users = userService.getAll().stream().map(SingleUserDto::new).collect(Collectors.toList());
        return HttpResponse.ok(new UserListDto(users));
    }

    @Get(value = "/users/{id}")
    public HttpResponse<SingleUserDto> getSingle(Long id) {
        return HttpResponse.ok(new SingleUserDto(userService.getById(id)));
    }

    @Post(value = "/users", uris = {"/users", "/register"})
    public HttpResponse<SingleUserDto> create(@Body SingleUserDto request, @QueryValue Optional<String> host) {
        String hostUnwrapped = host.orElse(null);
        User user = userService.create(request, hostUnwrapped);
        return HttpResponse.created(new SingleUserDto(user));
    }

    @Put(value = "/users/{id}")
    public HttpResponse<SingleUserDto> update(@Body SingleUserDto request, Long id) {
        User user = userService.update(id, request);
        return HttpResponse.ok(new SingleUserDto(user));
    }

    @Delete(value = "/users/{id}")
    public HttpResponse<Object> delete(Long id) {
        userService.delete(id);
        return HttpResponse.noContent();
    }

    @Get(value = "/email")
    public HttpResponse<Object> activateUser(String token) {
        checkDisabledVerification();
        String clientHost = userService.activateUser(token);
        return HttpResponse.redirect(URI.create(clientHost));
    }

    @Post(value = "/email")
    public HttpResponse<Object> resendEmail(ResendEmailRequest request) {
        checkDisabledVerification();
        userService.resendConfirmationEmail(request);
        return HttpResponse.ok();
    }

    private void checkDisabledVerification() {
        if (!emailService.isEnabled()) {
            String msg = "We were unable to process your request";
            String detail = "Email verification service is disabled";
            throw new ServiceDisabledException("verification", msg, detail);
        }
    }

    @Get(value = "/passwordReset")
    public HttpResponse<Object> initResetPassword(@QueryValue  String email, @QueryValue Optional<String> clientHost) {
        checkDisabledPasswordReset();
        userService.initiatePasswordReset(email, clientHost.orElse(null));
        return HttpResponse.ok();
    }

    @Post(value = "/passwordReset")
    public HttpResponse<Object> resetPassword(@Body ResetPasswordRequest request) {
        checkDisabledPasswordReset();
        String clientHost = userService.resetPassword(request);
        return HttpResponse.redirect(URI.create(clientHost));
    }

    private void checkDisabledPasswordReset() {
        if (!emailService.isEnabled()) {
            String msg = "We were unable to process your request";
            String detail = "Password reset service is disabled";
            throw new ServiceDisabledException("password", msg, detail);
        }
    }
}
