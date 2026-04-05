package com.epam.gymcrm.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=thisIsATestJwtSecretKeyThatIsLongEnough123",
        "app.jwt.expiration-ms=3600000"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerTraineeShouldBePublic() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        mockMvc.perform(post("/api/trainees")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John%s",
                                  "lastName": "Doe%s",
                                  "address": "Addr"
                                }
                                """.formatted(suffix, suffix)))
                .andExpect(status().isOk());
    }

    @Test
    void registerTrainerShouldBePublic() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        mockMvc.perform(post("/api/trainers")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Anna%s",
                                  "lastName": "Smith%s",
                                  "specialization": "YOGA"
                                }
                                """.formatted(suffix, suffix)))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isUnauthorized());
    }
}