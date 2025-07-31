package com.petcare.petcare_api.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import com.petcare.petcare_api.coredomain.service.WorkingPeriodService;
import com.petcare.petcare_api.utils.WorkingPeriodTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WorkingPeriodControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WorkingPeriodService workingPeriodService;

    @InjectMocks
    private WorkingPeriodController workingPeriodController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(workingPeriodController).build();
    }

    @Test
    void shouldCreateWorkingPeriod() throws Exception {
        WorkingPeriodRequestDTO request = WorkingPeriodTestFactory.buildRequest();
        WorkingPeriod entity = WorkingPeriodTestFactory.buildEntity();

        when(workingPeriodService.create(request)).thenReturn(entity);

        mockMvc.perform(post("/working-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId()))
                .andExpect(jsonPath("$.dayOfWeek").value(entity.getDayOfWeek().toString()))
                .andExpect(jsonPath("$.startTime").value(entity.getStartTime().toString()))
                .andExpect(jsonPath("$.endTime").value(entity.getEndTime().toString()));
    }

    @Test
    void shouldReturnAllWorkingPeriods() throws Exception {
        List<WorkingPeriod> periods = List.of(WorkingPeriodTestFactory.buildEntity());

        when(workingPeriodService.findAll()).thenReturn(periods);

        mockMvc.perform(get("/working-periods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(periods.getFirst().getId()));
    }

    @Test
    void shouldReturnWorkingPeriodById() throws Exception {
        WorkingPeriod entity = WorkingPeriodTestFactory.buildEntity();

        when(workingPeriodService.findById("1")).thenReturn(entity);

        mockMvc.perform(get("/working-periods/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId()));
    }

    @Test
    void shouldUpdateWorkingPeriod() throws Exception {
        WorkingPeriodRequestDTO request = WorkingPeriodTestFactory.buildRequest();
        WorkingPeriod updated = WorkingPeriodTestFactory.buildEntity();

        when(workingPeriodService.update("1", request)).thenReturn(updated);

        mockMvc.perform(put("/working-periods/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updated.getId()));
    }

    @Test
    void shouldDeleteWorkingPeriod() throws Exception {
        doNothing().when(workingPeriodService).delete("1");

        mockMvc.perform(delete("/working-periods/1"))
                .andExpect(status().isNoContent());

        verify(workingPeriodService, times(1)).delete("1");
    }
}
