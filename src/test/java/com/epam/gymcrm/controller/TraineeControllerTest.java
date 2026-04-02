package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TrainerSummaryResponse;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.facade.TraineeFacade;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TraineeFacade traineeFacade;

    @MockBean
    private SecurityAccessService securityAccessService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void registerTraineeShouldReturnCredentials() throws Exception {
        when(traineeFacade.createTraineeProfile("John", "Doe", null, "Addr"))
                .thenReturn(new GeneratedCredentials(UUID.randomUUID(), "john.doe", "secret"));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "address": "Addr"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("secret"));
    }

    @Test
    void updateTraineeShouldRequireActiveField() throws Exception {
        mockMvc.perform(put("/api/trainees/john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "address": "Addr"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTraineeShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/api/trainees/john.doe"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee profile deleted successfully"));

        verify(securityAccessService).ensureSameUser("john.doe");
        verify(traineeFacade).deleteTraineeProfile("john.doe");
    }

    @Test
    void getUnassignedTrainersShouldReturnSpecializationToo() throws Exception {
        when(traineeFacade.getNotAssignedActiveTrainers("john.doe"))
                .thenReturn(List.of(
                        new TrainerSummaryResponse("anna.smith", "Anna", "Smith", TrainingType.YOGA)
                ));

        mockMvc.perform(get("/api/trainees/john.doe/unassigned-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("anna.smith"))
                .andExpect(jsonPath("$[0].firstName").value("Anna"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].specialization").value("YOGA"));

        verify(securityAccessService).ensureSameUser("john.doe");
    }
}