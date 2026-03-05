package com.epam.gymcrm.util;

import com.epam.gymcrm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CredentialsGenerator {

    private static final String USERNAME_SEPARATOR = ".";
    private static final String PASSWORD_CHARS =
            "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";
    private static final int DEFAULT_PASSWORD_LENGTH = 10;

    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();
    private final int passwordLength;

    public CredentialsGenerator(
            UserRepository userRepository,
            @Value("${app.password.length:" + DEFAULT_PASSWORD_LENGTH + "}") int passwordLength
    ) {
        this.userRepository = userRepository;
        this.passwordLength = passwordLength;
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String f = normalizeName(firstName);
        String l = normalizeName(lastName);

        validateNamePart(f, "firstName");
        validateNamePart(l, "lastName");

        String base = f + USERNAME_SEPARATOR + l;

        String candidate = base;
        int suffix = 0;

        while (userRepository.existsByUsername(candidate)) {
            suffix++;
            candidate = base + suffix;
        }

        return candidate;
    }

    private void validateNamePart(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        if (value.length() < 2) {
            throw new IllegalArgumentException(field + " must be at least 2 characters");
        }
    }

    private String normalizeName(String raw) {
        if (raw == null) return null;

        String cleaned = raw.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) return "";

        String lower = cleaned.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    public String generatePassword() {
        int len = Math.max(1, passwordLength);

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int idx = random.nextInt(PASSWORD_CHARS.length());
            sb.append(PASSWORD_CHARS.charAt(idx));
        }
        return sb.toString();
    }
}