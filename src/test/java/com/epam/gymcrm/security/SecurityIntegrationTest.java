package com.epam.gymcrm.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerTraineeShouldBePublic() throws Exception {
        mockMvc.perform(post("/api/trainees")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "address": "Addr"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void registerTrainerShouldBePublic() throws Exception {
        mockMvc.perform(post("/api/trainers")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Anna",
                                  "lastName": "Smith",
                                  "specialization": "YOGA"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isUnauthorized());
    }
}