package com.petcare.petcare_api.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.petcare_api.application.dto.petServices.CreatePetServiceRequestDTO;
import com.petcare.petcare_api.application.dto.petServices.UpdatePetServiceRequestDTO;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.service.PetServiceService;
import com.petcare.petcare_api.utils.PetServiceTestFactory;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PetServiceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PetServiceService petServiceService;

    @InjectMocks
    private PetServiceController petServiceController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(petServiceController).build();
    }

    @Test
    void shouldCreatePetService() throws Exception {
        CreatePetServiceRequestDTO request = PetServiceTestFactory.buildCreateRequestDTO();
        PetService createdEntity = PetServiceTestFactory.buildEntity();
        when(petServiceService.create(any(CreatePetServiceRequestDTO.class))).thenReturn(createdEntity);

        mockMvc.perform(post("/pet-services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(createdEntity.getId()))
                .andExpect(jsonPath("$.name").value(createdEntity.getName()));
    }

    @Test
    void shouldUpdatePetService() throws Exception {
        String serviceId = "1";
        UpdatePetServiceRequestDTO request = new UpdatePetServiceRequestDTO("Nome Atualizado", "Desc Atualizada", 99.0, 75);
        PetService updatedEntity = PetServiceTestFactory.buildEntity();
        updatedEntity.setName("Nome Atualizado");
        when(petServiceService.update(eq(serviceId), any(UpdatePetServiceRequestDTO.class))).thenReturn(updatedEntity);

        mockMvc.perform(put("/pet-services/{id}", serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEntity.getId()))
                .andExpect(jsonPath("$.name").value("Nome Atualizado"));
    }

    @Test
    void shouldGetPetServiceById() throws Exception {
        String serviceId = "1";
        PetService entity = PetServiceTestFactory.buildEntity();
        when(petServiceService.getById(serviceId)).thenReturn(entity);

        mockMvc.perform(get("/pet-services/{id}", serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId()))
                .andExpect(jsonPath("$.name").value(entity.getName()));
    }

    @Test
    void shouldListPetServices() throws Exception {
        PetService entity = PetServiceTestFactory.buildEntity();
        PageImpl<PetService> page = new PageImpl<>(Collections.singletonList(entity), PageRequest.of(0, 10), 1);
        when(petServiceService.list(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/pet-services")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(entity.getId()));
    }

    @Test
    void shouldDeletePetService() throws Exception {
        String serviceId = "1";
        doNothing().when(petServiceService).delete(serviceId);

        mockMvc.perform(delete("/pet-services/{id}", serviceId))
                .andExpect(status().isNoContent());

        verify(petServiceService, times(1)).delete(serviceId);
    }
}