package com.epam.gymcrm.controller;

import com.epam.gymcrm.facade.UserFacade;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserFacade userFacade;

    @Test
    void loginShouldReturnSuccess() throws Exception {
        when(authService.authenticate(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(get("/api/users/login")
                        .param("username", "john.doe")
                        .param("password", "secret"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));

        verify(authService).authenticate("john.doe", "secret");
    }

    @Test
    void changePasswordShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john.doe",
                                  "oldPassword": "old123",
                                  "newPassword": "new123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));

        verify(userService).changePassword("john.doe", "old123", "new123");
    }

    @Test
    void activationShouldActivateUser() throws Exception {
        mockMvc.perform(patch("/api/users/john.doe/activation")
                        .param("password", "secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User activated"));

        verify(userFacade).activateUser("john.doe", "secret");
    }

    @Test
    void activationShouldDeactivateUser() throws Exception {
        mockMvc.perform(patch("/api/users/john.doe/activation")
                        .param("password", "secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isActive": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User deactivated"));

        verify(userFacade).deactivateUser("john.doe", "secret");
    }
}