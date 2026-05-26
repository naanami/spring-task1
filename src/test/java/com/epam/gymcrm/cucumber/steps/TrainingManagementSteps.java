package com.epam.gymcrm.cucumber.steps;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import io.cucumber.java.Before;
import com.epam.gymcrm.repository.TrainingRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class TrainingManagementSteps {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    private CreateTrainingRequest request;

    @Before
    public void cleanDatabase() {
        trainingRepository.deleteAll();
        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Given("valid training data")
    public void valid_training_data() {
        User traineeUser = new User("Lana", "Banana", "Lana.Banana", "password", true);
        User trainerUser = new User("John", "Doe", "John.Doe", "password", true);

        userRepository.save(traineeUser);
        userRepository.save(trainerUser);

        Trainee trainee = new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Yerevan");
        Trainer trainer = new Trainer(trainerUser, TrainingType.YOGA);

        traineeRepository.save(trainee);
        trainerRepository.save(trainer);

        request = new CreateTrainingRequest();
        request.setTraineeUsername("Lana.Banana");
        request.setTrainerUsername("John.Doe");
        request.setTrainingName("Morning Yoga");
        request.setTrainingType(TrainingType.YOGA);
        request.setTrainingDate(LocalDateTime.now().plusDays(1));
        request.setTrainingDuration(60);
    }

    @When("create training request is sent")
    public void create_training_request_is_sent() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .with(user("Lana.Banana"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Then("training should be created successfully")
    public void training_should_be_created_successfully() {
        assertThat(trainingRepository.count()).isEqualTo(1);
    }

    @Given("invalid training data")
    public void invalid_training_data() {
        request = new CreateTrainingRequest();
    }

    @When("invalid create training request is sent")
    public void invalid_create_training_request_is_sent() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .with(user("Lana.Banana"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Then("bad request response should be returned")
    public void bad_request_response_should_be_returned() {
    }
}