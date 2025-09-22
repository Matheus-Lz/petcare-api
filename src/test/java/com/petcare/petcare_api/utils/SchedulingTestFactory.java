package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.application.dto.scheduling.SchedulingRequestDTO;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.SchedulingRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SchedulingTestFactory {

    private final SchedulingRepository schedulingRepository;
    private final UserTestFactory userTestFactory;
    private final PetServiceTestFactory petServiceTestFactory;

    public SchedulingTestFactory(SchedulingRepository schedulingRepository, UserTestFactory userTestFactory, PetServiceTestFactory petServiceTestFactory) {
        this.schedulingRepository = schedulingRepository;
        this.userTestFactory = userTestFactory;
        this.petServiceTestFactory = petServiceTestFactory;
    }

    public static SchedulingRequestDTO buildRequestDTO() {
        return new SchedulingRequestDTO(
                "123e4567-e89b-12d3-a456-426614174000",
                LocalDateTime.of(2025, 8, 4, 10, 0)
        );
    }

    public static Scheduling buildEntity() {
        User user = UserTestFactory.buildUser();
        PetService petService = PetServiceTestFactory.buildEntity();

        return Scheduling.builder()
                .user(user)
                .petService(petService)
                .schedulingHour(LocalDateTime.of(2025, 8, 4, 10, 0))
                .status(SchedulingStatus.WAITING_FOR_ARRIVAL)
                .build();
    }
}