package com.epam.gymcrm.controller;

import com.epam.gymcrm.entity.TrainingType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/training-types")
@Api(tags = "Training Types")
public class TrainingTypeController {

    @GetMapping
    @ApiOperation("Get all training types")
    public List<TrainingType> getTrainingTypes() {
        return Arrays.asList(TrainingType.values());
    }
}