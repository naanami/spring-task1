package com.epam.gymcrm.facade;

import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.UserService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserFacadeTest {

    @Test
    void toggleUserActivationShouldAuthenticateThenDelegate() {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        UserFacade facade = new UserFacade(userService, authService);

        facade.toggleUserActivation("john.doe", "secret");

        verify(authService).authenticate("john.doe", "secret");
        verify(userService).toggleActive("john.doe");
    }
}