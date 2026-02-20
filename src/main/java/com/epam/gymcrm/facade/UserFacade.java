package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }
    public GeneratedCredentials registerUser(String firstName, String lastName) {
        return userService.registerUser(firstName, lastName);
    }
    public long countUsers() {
        return userService.countUsers();
    }

    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

}
