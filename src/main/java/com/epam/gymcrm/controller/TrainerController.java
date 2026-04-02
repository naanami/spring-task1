package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.TrainerRegistrationRequest;
import com.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TrainerProfileResponse;
import com.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.security.SecurityAccessService;
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
    private final SecurityAccessService securityAccessService;

    public TrainerController(TrainerFacade trainerFacade,
                             SecurityAccessService securityAccessService) {
        this.trainerFacade = trainerFacade;
        this.securityAccessService = securityAccessService;
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
    public TrainerProfileResponse getTrainerProfile(@PathVariable String username) {
        securityAccessService.ensureSameUser(username);
        return trainerFacade.getTrainerProfile(username);
    }

    @PutMapping("/{username}")
    @ApiOperation("Update trainer profile")
    public TrainerProfileResponse updateTrainerProfile(@PathVariable String username,
                                                       @Valid @RequestBody UpdateTrainerRequest request) {
        securityAccessService.ensureSameUser(username);
        return trainerFacade.updateTrainerProfile(
                username,
                request.getFirstName(),
                request.getLastName(),
                request.getActive()
        );
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation("Get trainer trainings with optional filters")
    public List<TrainerTrainingResponse> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String traineeName
    ) {
        securityAccessService.ensureSameUser(username);
        return trainerFacade.getTrainerTrainingResponses(
                username,
                from,
                to,
                traineeName
        );
    }
}