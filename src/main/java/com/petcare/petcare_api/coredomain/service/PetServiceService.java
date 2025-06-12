package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.petServices.CreatePetServiceRequestDTO;
import com.petcare.petcare_api.application.dto.petServices.PetServiceResponseDTO;
import com.petcare.petcare_api.application.dto.petServices.UpdatePetServiceRequestDTO;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.infrastructure.repository.PetServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PetServiceService {

    private final PetServiceRepository repository;

    @Autowired
    public PetServiceService(PetServiceRepository repository) {
        this.repository = repository;
    }

    public PetService create(CreatePetServiceRequestDTO requestDTO) {
        PetService petService = PetService.builder()
                .name(requestDTO.name())
                .description(requestDTO.description())
                .price(requestDTO.price())
                .time(requestDTO.time())
                .build();
        petService.validate();

        return repository.save(petService);
    }

    public PetService update(String id, UpdatePetServiceRequestDTO requestDTO) {
        PetService petService = this.getById(id);

        petService.setName(requestDTO.name());
        petService.setDescription(requestDTO.description());
        petService.setPrice(requestDTO.price());
        petService.setTime(requestDTO.time());
        petService.validate();

        return repository.save(petService);
    }

    public PetService getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PetService não encontrado"));
    }

    public Page<PetServiceResponseDTO> list(Integer page, Integer size) {
        Page<PetService> petServicePage = repository.findAllActive(PageRequest.of(page, size));
        return petServicePage.map(PetServiceResponseDTO::new);
    }

    public void delete(String id) {
        PetService petService = this.getById(id);
        petService.setDeleted(true);
        repository.save(petService);
    }
}
