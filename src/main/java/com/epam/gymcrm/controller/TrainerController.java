package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.TrainerRegistrationRequest;
import com.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TrainerProfileResponse;
import com.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.epam.gymcrm.facade.TrainerFacade;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@Api(tags = "Trainers")
public class TrainerController {

    private final TrainerFacade trainerFacade;

    public TrainerController(TrainerFacade trainerFacade) {
        this.trainerFacade = trainerFacade;
    }

    @PostMapping
    @ApiOperation("Register trainer profile")
    public GeneratedCredentials registerTrainer(@Valid @RequestBody TrainerRegistrationRequest request) {
        return trainerFacade.createTrainerProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecialization()
        );
    }

    @GetMapping("/{username}")
    @ApiOperation("Get trainer profile by username")
    public TrainerProfileResponse getTrainerProfile(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password
    ) {
        return trainerFacade.getTrainerProfile(username, password);
    }

    @PutMapping("/{username}")
    @ApiOperation("Update trainer profile")
    public TrainerProfileResponse updateTrainerProfile(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password,
            @Valid @RequestBody UpdateTrainerRequest request
    ) {
        return trainerFacade.updateTrainerProfile(
                username,
                password,
                request.getFirstName(),
                request.getLastName(),
                request.getActive()
        );
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation("Get trainer trainings with optional filters")
    public List<TrainerTrainingResponse> getTrainerTrainings(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String traineeName
    ) {
        return trainerFacade.getTrainerTrainingResponses(
                username,
                password,
                from,
                to,
                traineeName
        );
    }
}