package com.epam.gymcrm.facade;

import com.epam.gymcrm.service.UserService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserFacadeTest {

    @Test
    void toggleUserActivationShouldDelegate() {
        UserService userService = mock(UserService.class);
        UserFacade facade = new UserFacade(userService);

        facade.toggleUserActivation("john.doe");

        verify(userService).toggleActive("john.doe");
    }
}