package com.budgeteer.api.security;

import com.budgeteer.api.model.RefreshToken;
import com.budgeteer.api.repository.RefreshTokenRepository;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.event.ApplicationEvent;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.token.event.AccessTokenGeneratedEvent;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.generator.RefreshTokenGenerator;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.security.token.jwt.generator.AccessTokenConfiguration;
import io.micronaut.security.token.jwt.generator.DefaultAccessRefreshTokenGenerator;
import io.micronaut.security.token.jwt.generator.claims.ClaimsGenerator;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.micronaut.security.token.jwt.render.TokenRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Optional;

/**
 * An access refresh token generator that revokes the refresh token after it's use and generates a new one.
 */
@Singleton
@Replaces(DefaultAccessRefreshTokenGenerator.class)
public class RevokingAccessRefreshTokenGenerator extends DefaultAccessRefreshTokenGenerator {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    RefreshTokenRepository tokenRepository;
    /**
     * @param accessTokenConfiguration The access token generator config
     * @param tokenRenderer            The token renderer
     * @param tokenGenerator           The token generator
     * @param beanContext              Bean Context
     * @param refreshTokenGenerator    The refresh token generator
     * @param claimsGenerator          Claims generator
     * @param eventPublisher           The Application event publisher
     */
    public RevokingAccessRefreshTokenGenerator(AccessTokenConfiguration accessTokenConfiguration,
                                               TokenRenderer tokenRenderer,
                                               TokenGenerator tokenGenerator,
                                               BeanContext beanContext,
                                               RefreshTokenGenerator refreshTokenGenerator,
                                               ClaimsGenerator claimsGenerator,
                                               ApplicationEventPublisher eventPublisher,
                                               RefreshTokenRepository tokenRepository) {
        super(accessTokenConfiguration, tokenRenderer, tokenGenerator, beanContext, refreshTokenGenerator, claimsGenerator, eventPublisher);
        this.tokenRepository = tokenRepository;
    }

    @NonNull
    @Override
    public Optional<AccessRefreshToken> generate(@Nullable String refreshToken, @NonNull UserDetails userDetails) {
        Optional<String> optionalAccessToken = tokenGenerator.generateToken(userDetails, accessTokenExpiration(userDetails));
        if (optionalAccessToken.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to generate access token for user {}", userDetails.getUsername());
            }
            return Optional.empty();
        }
        String accessToken = optionalAccessToken.get();
        String key = refreshTokenGenerator.createKey(userDetails);
        Optional<String> optionalRefreshToken = refreshTokenGenerator.generate(userDetails, key);
        if (optionalRefreshToken.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to generate refresh token for user {}", userDetails.getUsername());
            }
            return Optional.empty();
        }
        revokeOldToken(refreshToken);
        refreshToken = optionalRefreshToken.get();
        eventPublisher.publishEvent(new RefreshTokenGeneratedEvent(userDetails, key));
        eventPublisher.publishEvent(new AccessTokenGeneratedEvent(accessToken));
        return Optional.of(tokenRenderer.render(userDetails, accessTokenExpiration(userDetails), accessToken, refreshToken));
    }

    private void revokeOldToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenOpt = tokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenOpt.isPresent()) {
            RefreshToken obj = refreshTokenOpt.get();
            obj.setRevoked(true);
            tokenRepository.update(obj);
        }
    }

}
