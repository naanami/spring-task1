package com.epam.gymcrm.cucumber.config;

import com.epam.gymcrm.Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {
}