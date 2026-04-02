package com.epam.gymcrm.facade;

import com.epam.gymcrm.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public void toggleUserActivation(String username) {
        userService.toggleActive(username);
    }
}