package com.budgeteer.api.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.DateCreated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @NonNull
    @NotBlank
    @Column(name = "refresh_token")
    private String refreshToken;

    @NonNull
    private Boolean revoked;

    @DateCreated
    @NonNull
    @Column(name = "date_created")
    private Instant dateCreated;

    public RefreshToken() {
    }

    public RefreshToken(User user, String refreshToken, Boolean revoked) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.revoked = revoked;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NonNull
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(@NonNull String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @NonNull
    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(@NonNull Boolean revoked) {
        this.revoked = revoked;
    }

    @NonNull
    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(@NonNull Instant dateCreated) {
        this.dateCreated = dateCreated;
    }
}
