package com.epam.gymcrm.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityAccessService {

    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public void ensureSameUser(String username) {
        String authenticatedUsername = getAuthenticatedUsername();
        if (!authenticatedUsername.equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}