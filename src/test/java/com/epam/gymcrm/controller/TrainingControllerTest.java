package com.epam.gymcrm.controller;

import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.security.CustomUserDetailsService;
import com.epam.gymcrm.security.JwtService;
import com.epam.gymcrm.security.SecurityAccessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingFacade trainingFacade;

    @MockBean
    private SecurityAccessService securityAccessService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createTrainingShouldReturnSuccess() throws Exception {
        when(securityAccessService.getAuthenticatedUsername()).thenReturn("john.doe");

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "john.doe",
                                  "trainerUsername": "anna.smith",
                                  "trainingName": "Morning Yoga",
                                  "trainingType": "YOGA",
                                  "trainingDate": "2026-03-20T09:00:00",
                                  "trainingDuration": 60
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Training created successfully"));

        verify(trainingFacade).createTraining(
                "john.doe",
                "john.doe",
                "anna.smith",
                "Morning Yoga",
                com.epam.gymcrm.entity.TrainingType.YOGA,
                java.time.LocalDateTime.of(2026, 3, 20, 9, 0),
                60
        );
    }
}