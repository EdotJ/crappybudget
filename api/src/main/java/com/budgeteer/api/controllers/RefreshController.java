package com.budgeteer.api.controllers;

import com.budgeteer.api.core.EmailConfig;
import com.budgeteer.api.model.RefreshToken;
import com.budgeteer.api.repository.RefreshTokenRepository;
import com.budgeteer.api.security.IdentifierUserDetails;
import io.micronaut.http.server.exceptions.InternalServerException;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.errors.IssuingAnAccessTokenErrorCode;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class RefreshController implements RefreshTokenPersistence {

    private final RefreshTokenRepository repository;
    private final boolean isVerificationEnabled;

    public RefreshController(RefreshTokenRepository repository, EmailConfig emailConfig) {
        this.repository = repository;
        this.isVerificationEnabled = emailConfig.isEnabled();
    }

    @Override
    @EventListener
    public void persistToken(RefreshTokenGeneratedEvent event) {
        if (event == null || !(event.getUserDetails() instanceof IdentifierUserDetails)) {
            throw new InternalServerException("Error occurred while persisting refresh token");
        }
        IdentifierUserDetails details = (IdentifierUserDetails) event.getUserDetails();
        if (event.getRefreshToken() != null
                && details != null
                && details.getId() != null
                && details.getUsername() != null) {
            String token = event.getRefreshToken();
            repository.save(details.getId(), token, Boolean.FALSE);
        }
    }

    @Override
    public Publisher<UserDetails> getUserDetails(String refreshToken) {
        return Flowable.create(emitter -> {
            Optional<RefreshToken> tokenOpt = repository.findByRefreshToken(refreshToken);
            if (tokenOpt.isPresent()) {
                handleToken(emitter, tokenOpt);
            } else {
                String msg = "Refresh token not found";
                IssuingAnAccessTokenErrorCode errorCode = IssuingAnAccessTokenErrorCode.INVALID_GRANT;
                emitter.onError(new OauthErrorResponseException(errorCode, msg, null));
            }
        }, BackpressureStrategy.ERROR);
    }

    private void handleToken(io.reactivex.FlowableEmitter<UserDetails> emitter, Optional<RefreshToken> tokenOpt) {
        RefreshToken token = tokenOpt.get();
        if (token.getRevoked()) {
            String msg = "Refresh token revoked";
            emitter.onError(new OauthErrorResponseException(IssuingAnAccessTokenErrorCode.INVALID_GRANT, msg, null));
        } else {
            token.setRevoked(true);
            repository.update(token);
            List<String> roles = new ArrayList<>();
            if (token.getUser().isVerified() || !isVerificationEnabled) {
                roles.add("ROLE_VERIFIED");
            }
            emitter.onNext(new IdentifierUserDetails(token.getUser().getUsername(), roles, token.getUser().getId()));
            emitter.onComplete();
        }
    }
}
