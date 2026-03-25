package com.epam.gymcrm.facade;

import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    private final UserService userService;
    private final AuthService authService;

    public UserFacade(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    public void toggleUserActivation(String username, String password) {
        authService.authenticate(username, password);
        userService.toggleActive(username);
    }
}