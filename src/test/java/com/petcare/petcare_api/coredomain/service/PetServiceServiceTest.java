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
                .orElseThrow(() -> new AssertionError("PetService não encontrado no banco após o soft delete."));
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

    @Test
    void shouldThrowWhenGetByIdNotFound() {
        assertThrows(IllegalArgumentException.class, () -> petServiceService.getById("nao-existe"));
    }

    @Test
    void shouldThrowWhenUpdateNotFound() {
        UpdatePetServiceRequestDTO dto = new UpdatePetServiceRequestDTO("n", "d", 10.0, 10);
        assertThrows(IllegalArgumentException.class, () -> petServiceService.update("nao-existe", dto));
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        assertThrows(IllegalArgumentException.class, () -> petServiceService.delete("nao-existe"));
    }

    @Test
    void shouldRejectUpdateWithInvalidData() {
        PetService s = petServiceService.create(
                new CreatePetServiceRequestDTO("A", "B", 10.0, 10)
        );
        UpdatePetServiceRequestDTO invalid =
                new UpdatePetServiceRequestDTO("", "", -1.0, 0);

        var petServiceId = s.getId();

        assertThrows(IllegalArgumentException.class,
                () -> petServiceService.update(petServiceId, invalid));
    }

    @Test
    void shouldKeepPaginationStable() {
        for (int i = 1; i <= 7; i++) {
            petServiceService.create(new CreatePetServiceRequestDTO("S"+i, "D"+i, i * 5.0, i * 5));
        }
        Page<PetService> page0 = petServiceService.list(0, 3);
        Page<PetService> page1 = petServiceService.list(1, 3);
        assertEquals(3, page0.getContent().size());
        assertEquals(3, page1.getContent().size());
        assertNotEquals(page0.getContent().getFirst().getId(), page1.getContent().getFirst().getId());
    }

    @Test
    void shouldUpdateOnlyWhenValid() {
        PetService s = petServiceService.create(new CreatePetServiceRequestDTO("Nome", "Desc", 50.0, 30));
        UpdatePetServiceRequestDTO dto = new UpdatePetServiceRequestDTO("Novo", "Nova", 70.0, 45);
        PetService after = petServiceService.update(s.getId(), dto);
        assertEquals("Novo", after.getName());
        assertEquals("Nova", after.getDescription());
        assertEquals(70.0, after.getPrice());
        assertEquals(45, after.getTime());
    }

    @Test
    void shouldNotListDeleted() {
        PetService s1 = petServiceService.create(new CreatePetServiceRequestDTO("Ativo", "A", 10.0, 10));
        PetService s2 = petServiceService.create(new CreatePetServiceRequestDTO("Deletar", "B", 20.0, 20));
        petServiceService.delete(s2.getId());
        Page<PetService> page = petServiceService.list(0, 10);
        assertTrue(page.getContent().stream().anyMatch(p -> p.getId().equals(s1.getId())));
        assertFalse(page.getContent().stream().anyMatch(p -> p.getId().equals(s2.getId())));
    }
}
