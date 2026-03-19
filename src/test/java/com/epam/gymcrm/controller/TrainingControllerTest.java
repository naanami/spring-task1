package com.epam.gymcrm.controller;

import com.epam.gymcrm.facade.TrainingFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingFacade trainingFacade;

    @Test
    void createTrainingShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .param("username", "john.doe")
                        .param("password", "secret")
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
                "secret",
                "john.doe",
                "anna.smith",
                "Morning Yoga",
                com.epam.gymcrm.entity.TrainingType.YOGA,
                java.time.LocalDateTime.of(2026, 3, 20, 9, 0),
                60
        );
    }
}