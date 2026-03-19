package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.epam.gymcrm.facade.TrainingFacade;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainings")
@Api(tags = "Trainings")
public class TrainingController {

    private final TrainingFacade trainingFacade;

    public TrainingController(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @PostMapping
    @ApiOperation("Create training")
    public String createTraining(
            @ApiParam(value = "Authenticated username", required = true)
            @RequestParam String username,
            @ApiParam(value = "Authenticated user password", required = true)
            @RequestParam String password,
            @Valid @RequestBody CreateTrainingRequest request
    ) {
        trainingFacade.createTraining(
                username,
                password,
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