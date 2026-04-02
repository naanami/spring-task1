package com.epam.gymcrm.controller;

import com.epam.gymcrm.facade.UserFacade;
import com.epam.gymcrm.security.CustomUserDetailsService;
import com.epam.gymcrm.security.JwtService;
import com.epam.gymcrm.security.SecurityAccessService;
import com.epam.gymcrm.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserFacade userFacade;

    @MockBean
    private SecurityAccessService securityAccessService;

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

        verify(securityAccessService).ensureSameUser("john.doe");
        verify(userService).changePassword("john.doe", "old123", "new123");
    }

    @Test
    void toggleActivationShouldReturnSuccess() throws Exception {
        mockMvc.perform(patch("/api/users/john.doe/activation"))
                .andExpect(status().isOk())
                .andExpect(content().string("User activation status changed successfully"));

        verify(securityAccessService).ensureSameUser("john.doe");
        verify(userFacade).toggleUserActivation("john.doe");
    }
}