package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.petservices.CreatePetServiceRequestDTO;
import com.petcare.petcare_api.application.dto.petservices.UpdatePetServiceRequestDTO;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.infrastructure.repository.PetServiceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class PetServiceServiceTest {

    @Autowired
    private PetServiceService petServiceService;

    @Autowired
    private PetServiceRepository petServiceRepository;

    @Test
    void shouldCreatePetServiceSuccessfully() {
        CreatePetServiceRequestDTO dto = new CreatePetServiceRequestDTO("Banho e Tosa", "Serviço completo", 75.0, 60);

        PetService createdService = petServiceService.create(dto);

        assertNotNull(createdService.getId());
        assertEquals("Banho e Tosa", createdService.getName());
        assertTrue(petServiceRepository.findById(createdService.getId()).isPresent());
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithInvalidData() {
        CreatePetServiceRequestDTO dto = new CreatePetServiceRequestDTO("", "Descrição", 0.0, 0);

        assertThrows(IllegalArgumentException.class, () -> petServiceService.create(dto));
    }

    @Test
    void shouldUpdatePetServiceSuccessfully() {
        CreatePetServiceRequestDTO createDto = new CreatePetServiceRequestDTO("Consulta Veterinária", "Check-up geral", 150.0, 30);
        PetService service = petServiceService.create(createDto);

        UpdatePetServiceRequestDTO updateDto = new UpdatePetServiceRequestDTO("Consulta Especializada", "Check-up com especialista", 250.0, 45);
        PetService updatedService = petServiceService.update(service.getId(), updateDto);

        assertEquals("Consulta Especializada", updatedService.getName());
        assertEquals(250.0, updatedService.getPrice());
        assertEquals(45, updatedService.getTime());
    }

    @Test
    void shouldFindPetServiceById() {
        PetService createdService = petServiceService.create(new CreatePetServiceRequestDTO("Vacinação V10", "Imunização", 80.0, 15));

        PetService foundService = petServiceService.getById(createdService.getId());

        assertNotNull(foundService);
        assertEquals(createdService.getId(), foundService.getId());
    }

    @Test
    void shouldSoftDeletePetService() {
        PetService service = petServiceService.create(new CreatePetServiceRequestDTO("Microchipagem", "Identificação", 120.0, 20));

        petServiceService.delete(service.getId());

        PetService deletedPetService = petServiceRepository.findById(service.getId())
                .orElseThrow(() -> new AssertionError("Funcionário não encontrado no banco após o soft delete."));

        assertTrue(deletedPetService.isDeleted());
    }

    @Test
    void shouldListPetServices() {
        petServiceService.create(new CreatePetServiceRequestDTO("Serviço 1", "Desc 1", 10.0, 10));
        petServiceService.create(new CreatePetServiceRequestDTO("Serviço 2", "Desc 2", 20.0, 20));

        Page<PetService> servicesPage = petServiceService.list(0, 5);

        assertEquals(2, servicesPage.getTotalElements());
        assertEquals("Serviço 1", servicesPage.getContent().getFirst().getName());
    }
}