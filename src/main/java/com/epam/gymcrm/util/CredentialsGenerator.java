package com.epam.gymcrm.util;

import com.epam.gymcrm.dao.UserDao;
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

        if (f.isEmpty() || l.isEmpty()) {
            throw new IllegalArgumentException("first name and last name are required");
        }

        String base = f + USERNAME_SEPARATOR + l;

        if (!userDao.existsByUsername(base)) {
            return base;
        }

        int suffix = 1;
        String candidate = base + suffix;
        while (userDao.existsByUsername(candidate)) {
            suffix++;
            candidate = base + suffix;
        }
        return candidate;
    }

    private String normalizeName(String raw) {
        if(raw == null){
            return "";
        }
        String cleaned = raw.trim().replaceAll("\\s+", " ");
        if(cleaned.isEmpty()){
            return "";
        }
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
