package com.epam.gymcrm.util;

import com.epam.gymcrm.dao.UserDao;
import com.epam.gymcrm.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CredentialsGenerator {

    private static final String USERNAME_SEPARATOR = ".";
    private static final String PASSWORD_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";
    private static final int DEFAULT_PASSWORD_LENGTH = 10;

    private final UserDao userDao;
    private final SecureRandom random = new SecureRandom();
    private final int passwordLength;

    public CredentialsGenerator(UserDao userDao, @Value("${app.password.length:" +  DEFAULT_PASSWORD_LENGTH + "}")  int passwordLength) {
        this.userDao = userDao;
        this.passwordLength = passwordLength;
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String f = normalizeName(firstName);
        String l = normalizeName(lastName);

        validateNamePart(f, "firstName");
        validateNamePart(l, "lastName");

        String base = f + USERNAME_SEPARATOR + l;

        var matches = userDao.findByUsernamePrefix(base);

        if (matches.isEmpty()) {
            return base;
        }

        int maxSuffix = matches.stream()
                .map(User::getUsername)
                .mapToInt(u -> extractSuffix(u, base))
                .max()
                .orElse(0);

        return base + (maxSuffix + 1);
    }

    private int extractSuffix(String username, String base) {
        if (username.equals(base)) return 0;
        String rest = username.substring(base.length());
        if (rest.isEmpty()) return 0;
        if (!rest.chars().allMatch(Character::isDigit)) return 0;
        return Integer.parseInt(rest);
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
