package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class UserFacadeTest {

    @Test
    void registerUserShouldDelegateToService() {
        UserService service = mock(UserService.class);
        UserFacade facade = new UserFacade(service);

        GeneratedCredentials expected =
                new GeneratedCredentials(UUID.randomUUID(), "A.B", "secret");
        when(service.registerUser("A", "B")).thenReturn(expected);

        GeneratedCredentials actual = facade.registerUser("A", "B");

        assertSame(expected, actual);
        verify(service).registerUser("A", "B");
        verifyNoMoreInteractions(service);
    }
}
