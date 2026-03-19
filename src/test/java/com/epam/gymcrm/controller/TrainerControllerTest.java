package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.facade.TrainerFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerFacade trainerFacade;

    @Test
    void registerTrainerShouldReturnCredentials() throws Exception {
        when(trainerFacade.createTrainerProfile("Anna", "Smith", TrainingType.YOGA))
                .thenReturn(new GeneratedCredentials(UUID.randomUUID(), "anna.smith", "secret"));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Anna",
                                  "lastName": "Smith",
                                  "specialization": "YOGA"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anna.smith"))
                .andExpect(jsonPath("$.password").value("secret"));
    }
}