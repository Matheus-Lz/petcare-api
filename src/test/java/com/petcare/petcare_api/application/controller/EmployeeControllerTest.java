package com.petcare.petcare_api.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.petcare_api.application.dto.employee.CreateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.employee.UpdateEmployeeRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.service.EmployeeService;
import com.petcare.petcare_api.utils.EmployeeTestFactory;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        CreateEmployeeRequestDTO request = EmployeeTestFactory.buildCreateRequestDTO();
        Employee createdEntity = EmployeeTestFactory.buildEmployee();
        when(employeeService.create(any(CreateEmployeeRequestDTO.class))).thenReturn(createdEntity);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(createdEntity.getId()));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        String employeeId = "1";
        UpdateEmployeeRequestDTO request = new UpdateEmployeeRequestDTO(List.of(UUID.randomUUID().toString()));
        Employee updatedEntity = EmployeeTestFactory.buildEmployee();
        when(employeeService.update(eq(employeeId), any(UpdateEmployeeRequestDTO.class))).thenReturn(updatedEntity);

        mockMvc.perform(put("/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEntity.getId()));
    }

    @Test
    void shouldGetEmployeeById() throws Exception {
        String employeeId = "1";
        Employee entity = EmployeeTestFactory.buildEmployee();
        when(employeeService.getById(employeeId)).thenReturn(entity);

        mockMvc.perform(get("/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId()));
    }



    @Test
    void shouldListEmployees() throws Exception {
        Employee entity = EmployeeTestFactory.buildEmployee();
        PageImpl<Employee> page = new PageImpl<>(Collections.singletonList(entity), PageRequest.of(0, 10), 1);
        when(employeeService.list(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/employees")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(entity.getId()));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        String employeeId = "1";
        doNothing().when(employeeService).delete(employeeId);

        mockMvc.perform(delete("/employees/{id}", employeeId))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).delete(employeeId);
    }
}