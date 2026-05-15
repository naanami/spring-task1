package com.epam.gymcrm.config;

import com.epam.gymcrm.dto.message.TrainerWorkloadMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.jms.support.converter.MessageConverter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JmsConfigTest {

    private final JmsConfig config = new JmsConfig();

    @Test
    void jacksonJmsMessageConverterShouldCreateJsonTextMessage() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        MessageConverter converter = config.jacksonJmsMessageConverter(objectMapper);

        TrainerWorkloadMessage message = new TrainerWorkloadMessage();
        message.setTrainerUsername("John.Doe");
        message.setTrainerFirstName("John");
        message.setTrainerLastName("Doe");
        message.setActive(true);
        message.setTrainingDate(LocalDate.of(2026, 5, 10));
        message.setTrainingDuration(60);

        Session session = mock(Session.class);
        TextMessage textMessage = mock(TextMessage.class);

        when(session.createTextMessage(anyString())).thenReturn(textMessage);

        jakarta.jms.Message result = converter.toMessage(message, session);

        assertNotNull(result);
        verify(session).createTextMessage(contains("John.Doe"));
    }
}