package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.TraineeRegistrationRequest;
import com.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.epam.gymcrm.dto.request.UpdateTraineeTrainersRequest;
import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TraineeProfileResponse;
import com.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.epam.gymcrm.dto.response.TrainerSummaryResponse;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.security.SecurityAccessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@Api(tags = "Trainees")
public class TraineeController {

    private final TraineeFacade traineeFacade;
    private final SecurityAccessService securityAccessService;

    public TraineeController(TraineeFacade traineeFacade,
                             SecurityAccessService securityAccessService) {
        this.traineeFacade = traineeFacade;
        this.securityAccessService = securityAccessService;
    }

    @PostMapping
    @ApiOperation("Register trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee registered successfully"),
            @ApiResponse(code = 400, message = "Invalid trainee input")
    })
    public GeneratedCredentials registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {
        return traineeFacade.createTraineeProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress()
        );
    }

    @GetMapping("/{username}")
    @ApiOperation("Get trainee profile by username")
    public TraineeProfileResponse getTraineeProfile(@PathVariable String username) {
        securityAccessService.ensureSameUser(username);
        return traineeFacade.getTraineeProfile(username);
    }

    @PutMapping("/{username}")
    @ApiOperation("Update trainee profile")
    public TraineeProfileResponse updateTraineeProfile(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeRequest request
    ) {
        securityAccessService.ensureSameUser(username);

        return traineeFacade.updateTraineeProfile(
                username,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                request.getActive()
        );
    }

    @DeleteMapping("/{username}")
    @ApiOperation("Delete trainee profile")
    public String deleteTraineeProfile(@PathVariable String username) {
        securityAccessService.ensureSameUser(username);
        traineeFacade.deleteTraineeProfile(username);
        return "Trainee profile deleted successfully";
    }

    @GetMapping("/{username}/unassigned-trainers")
    @ApiOperation("Get not assigned active trainers for trainee")
    public List<TrainerSummaryResponse> getNotAssignedActiveTrainers(@PathVariable String username) {
        securityAccessService.ensureSameUser(username);
        return traineeFacade.getNotAssignedActiveTrainers(username);
    }

    @PutMapping("/{username}/trainers")
    @ApiOperation("Update trainee trainers list")
    public List<TrainerSummaryResponse> updateTraineeTrainers(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest request
    ) {
        securityAccessService.ensureSameUser(username);
        return traineeFacade.updateTraineeTrainers(username, request.getTrainerUsernames());
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation("Get trainee trainings with optional filters")
    public List<TraineeTrainingResponse> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) TrainingType type
    ) {
        securityAccessService.ensureSameUser(username);

        return traineeFacade.getTraineeTrainingResponses(
                username,
                from,
                to,
                trainerName,
                type
        );
    }
}