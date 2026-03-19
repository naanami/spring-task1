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
import io.swagger.annotations.*;
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

    public TraineeController(TraineeFacade traineeFacade) {
        this.traineeFacade = traineeFacade;
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile returned"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public TraineeProfileResponse getTraineeProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password
    ) {
        return traineeFacade.getTraineeProfile(username, password);
    }

    @PutMapping("/{username}")
    @ApiOperation("Update trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile updated"),
            @ApiResponse(code = 400, message = "Invalid trainee input"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public TraineeProfileResponse updateTraineeProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password,
            @Valid @RequestBody UpdateTraineeRequest request
    ) {
        return traineeFacade.updateTraineeProfile(
                username,
                password,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                request.getActive()
        );
    }

    @DeleteMapping("/{username}")
    @ApiOperation("Delete trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile deleted"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public String deleteTraineeProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password
    ) {
        traineeFacade.deleteTraineeProfile(username, password);
        return "Trainee profile deleted successfully";
    }

    @GetMapping("/{username}/unassigned-trainers")
    @ApiOperation("Get not assigned active trainers for trainee")
    public List<TrainerSummaryResponse> getNotAssignedActiveTrainers(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password
    ) {
        return traineeFacade.getNotAssignedActiveTrainers(username, password);
    }

    @PutMapping("/{username}/trainers")
    @ApiOperation("Update trainee trainers list")
    public List<TrainerSummaryResponse> updateTraineeTrainers(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password,
            @Valid @RequestBody UpdateTraineeTrainersRequest request
    ) {
        return traineeFacade.updateTraineeTrainers(
                username,
                password,
                request.getTrainerUsernames()
        );
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation("Get trainee trainings with optional filters")
    public List<TraineeTrainingResponse> getTraineeTrainings(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) TrainingType type
    ) {
        return traineeFacade.getTraineeTrainingResponses(
                username,
                password,
                from,
                to,
                trainerName,
                type
        );
    }
}