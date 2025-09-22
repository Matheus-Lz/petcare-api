package com.petcare.petcare_api.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.petcare.petcare_api.application.dto.scheduling.SchedulingRequestDTO;
import com.petcare.petcare_api.application.dto.scheduling.UpdateSchedulingStatusRequestDTO;
import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;
import com.petcare.petcare_api.coredomain.service.SchedulingService;
import com.petcare.petcare_api.utils.SchedulingTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SchedulingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SchedulingService schedulingService;

    @InjectMocks
    private SchedulingController schedulingController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(schedulingController).build();
    }

    @Test
    void shouldCreateScheduling() throws Exception {
        SchedulingRequestDTO requestDTO = SchedulingTestFactory.buildRequestDTO();
        Scheduling scheduling = SchedulingTestFactory.buildEntity();

        when(schedulingService.create(any(SchedulingRequestDTO.class))).thenReturn(scheduling);

        mockMvc.perform(post("/schedulings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/schedulings/")))
                .andExpect(jsonPath("$.id").value(scheduling.getId()));
    }

    @Test
    void shouldUpdateScheduling() throws Exception {
        String schedulingId = "1";
        SchedulingRequestDTO requestDTO = SchedulingTestFactory.buildRequestDTO();
        Scheduling updatedScheduling = SchedulingTestFactory.buildEntity();

        when(schedulingService.update(eq(schedulingId), any(SchedulingRequestDTO.class))).thenReturn(updatedScheduling);

        mockMvc.perform(put("/schedulings/{id}", schedulingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedScheduling.getId()));
    }

    @Test
    void shouldFindSchedulingById() throws Exception {
        String schedulingId = "1";
        Scheduling scheduling = SchedulingTestFactory.buildEntity();

        when(schedulingService.findById(schedulingId)).thenReturn(scheduling);

        mockMvc.perform(get("/schedulings/{id}", schedulingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduling.getId()));
    }

    @Test
    void shouldDeleteScheduling() throws Exception {
        String schedulingId = "1";
        doNothing().when(schedulingService).delete(schedulingId);

        mockMvc.perform(delete("/schedulings/{id}", schedulingId))
                .andExpect(status().isNoContent());

        verify(schedulingService, times(1)).delete(schedulingId);
    }

    @Test
    void shouldGetAvailableTimes() throws Exception {
        String petServiceId = "1";
        LocalDate date = LocalDate.of(2025, 8, 3);
        List<LocalTime> availableTimes = List.of(LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(schedulingService.getAvailableTimes(petServiceId, date)).thenReturn(availableTimes);

        mockMvc.perform(get("/schedulings/available-times")
                        .param("petServiceId", petServiceId)
                        .param("date", date.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenDateParamIsInvalid() throws Exception {
        String petServiceId = "1";

        mockMvc.perform(get("/schedulings/available-times")
                        .param("petServiceId", petServiceId)
                        .param("date", "2025-13-99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenMissingRequiredParam() throws Exception {
        LocalDate date = LocalDate.of(2025, 8, 3);

        mockMvc.perform(get("/schedulings/available-times")
                        .param("date", date.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAvailableDays() throws Exception {
        String petServiceId = "1";
        LocalDate monthStart = LocalDate.of(2025, 8, 1);
        List<LocalDate> availableDays = List.of(LocalDate.of(2025, 8, 5), LocalDate.of(2025, 8, 10));

        when(schedulingService.getAvailableDays(petServiceId, monthStart)).thenReturn(availableDays);

        mockMvc.perform(get("/schedulings/available-days")
                        .param("petServiceId", petServiceId)
                        .param("monthStart", monthStart.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindUserSchedulings() throws Exception {
        Scheduling s = SchedulingTestFactory.buildEntity();
        PageImpl<Scheduling> page = new PageImpl<>(Collections.singletonList(s), PageRequest.of(0, 10), 1);
        when(schedulingService.findByCurrentUser(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/schedulings/user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldFindUserSchedulingsWithDefaultPaging() throws Exception {
        Scheduling s = SchedulingTestFactory.buildEntity();
        PageImpl<Scheduling> page = new PageImpl<>(Collections.singletonList(s), PageRequest.of(0, 10), 1);
        when(schedulingService.findByCurrentUser(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/schedulings/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldDelegateToUser() throws Exception {
        String schedulingId = "1";
        String employeeId = "2";

        doNothing().when(schedulingService).delegateToUser(schedulingId, employeeId);

        mockMvc.perform(patch("/schedulings/{id}/delegate/{employeeId}", schedulingId, employeeId))
                .andExpect(status().isNoContent());

        verify(schedulingService, times(1)).delegateToUser(schedulingId, employeeId);
    }

    @Test
    void shouldUpdateStatus() throws Exception {
        String schedulingId = "1";
        UpdateSchedulingStatusRequestDTO request = new UpdateSchedulingStatusRequestDTO(SchedulingStatus.COMPLETED);
        doNothing().when(schedulingService).updateStatus(schedulingId, SchedulingStatus.COMPLETED);

        mockMvc.perform(patch("/schedulings/{id}/status", schedulingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(schedulingService, times(1)).updateStatus(schedulingId, SchedulingStatus.COMPLETED);
    }
}
