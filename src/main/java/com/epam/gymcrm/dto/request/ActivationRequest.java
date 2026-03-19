package com.epam.gymcrm.dto.request;

import jakarta.validation.constraints.NotNull;

public class ActivationRequest {

    @NotNull
    private Boolean isActive;

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}