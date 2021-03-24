package com.budgeteer.api.repository;

import com.budgeteer.api.model.RefreshToken;
import com.budgeteer.api.model.User;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public abstract class RefreshTokenRepository implements CrudRepository<RefreshToken, Long> {

    EntityManager em;

    public RefreshTokenRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public RefreshToken save(@NonNull Long userId,
                      @NonNull @NotBlank String refreshToken,
                      @NonNull @NotNull Boolean revoked) {
        User user = em.getReference(User.class, userId);
        RefreshToken token = new RefreshToken(user, refreshToken, revoked);
        return this.save(token);
    }

    public abstract Optional<RefreshToken> findByRefreshToken(@NonNull @NotBlank String refreshToken);
}
