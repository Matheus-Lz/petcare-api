package com.petcare.petcare_api.application.dto.scheduling;

import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;

public record UpdateSchedulingStatusRequestDTO(SchedulingStatus status) {}
