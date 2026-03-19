package com.epam.gymcrm.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UpdateTraineeTrainersRequest {

    @NotNull
    private List<String> trainerUsernames;

    public List<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(List<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}