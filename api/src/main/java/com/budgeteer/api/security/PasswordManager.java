package com.budgeteer.api.security;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;

@Singleton
public class PasswordManager {

    private final PasswordEncoder passwordEncoder;

    public PasswordManager() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encode(@NotBlank @NonNull String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(@NotBlank @NonNull String rawPassword, @NotBlank @NonNull String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
