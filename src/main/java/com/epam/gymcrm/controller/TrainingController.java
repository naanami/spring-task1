package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.security.SecurityAccessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainings")
@Api(tags = "Trainings")
public class TrainingController {

    private final TrainingFacade trainingFacade;
    private final SecurityAccessService securityAccessService;

    public TrainingController(TrainingFacade trainingFacade,
                              SecurityAccessService securityAccessService) {
        this.trainingFacade = trainingFacade;
        this.securityAccessService = securityAccessService;
    }

    @PostMapping
    @ApiOperation("Create training")
    public String createTraining(@Valid @RequestBody CreateTrainingRequest request) {
        String authenticatedUsername = securityAccessService.getAuthenticatedUsername();

        trainingFacade.createTraining(
                authenticatedUsername,
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingName(),
                request.getTrainingType(),
                request.getTrainingDate(),
                request.getTrainingDuration()
        );

        return "Training created successfully";
    }
}