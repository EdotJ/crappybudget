package com.budgeteer.api.repository;

import com.budgeteer.api.model.RefreshToken;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    @Transactional
    RefreshToken save(@NonNull @NotBlank Long userId,
                      @NonNull @NotBlank String username,
                      @NonNull @NotBlank String refreshToken,
                      @NonNull @NotNull Boolean revoked);

    Optional<RefreshToken> findByRefreshToken(@NonNull @NotBlank String refreshToken);
}
