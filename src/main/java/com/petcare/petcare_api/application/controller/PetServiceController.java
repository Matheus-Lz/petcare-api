package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.petServices.CreatePetServiceRequestDTO;
import com.petcare.petcare_api.application.dto.petServices.PetServiceResponseDTO;
import com.petcare.petcare_api.application.dto.petServices.UpdatePetServiceRequestDTO;
import com.petcare.petcare_api.coredomain.service.PetServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pet-services")
public class PetServiceController {

    private final PetServiceService service;

    @Autowired
    public PetServiceController(PetServiceService service) {
        this.service = service;
    }

    @Operation(summary = "Cria um novo serviço de pet", description = "Cria um novo serviço de pet com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço de pet criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<PetServiceResponseDTO> create(@RequestBody @Parameter(description = "Dados para criar um serviço de pet") CreatePetServiceRequestDTO requestDTO) {
        PetServiceResponseDTO responseDTO = new PetServiceResponseDTO(service.create(requestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Atualiza um serviço de pet existente", description = "Atualiza os dados de um serviço de pet existente baseado no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço de pet atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço de pet não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PetServiceResponseDTO> update(@PathVariable @Parameter(description = "ID do serviço de pet a ser atualizado") String id,
                                                                  @RequestBody @Parameter(description = "Dados para atualizar o serviço de pet") UpdatePetServiceRequestDTO requestDTO) {
        PetServiceResponseDTO responseDTO = new PetServiceResponseDTO(service.update(id, requestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Recupera um serviço de pet pelo ID", description = "Recupera as informações de um serviço de pet específico com base no ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço de pet encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço de pet não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PetServiceResponseDTO> getById(@PathVariable @Parameter(description = "ID do serviço de pet") String id) {
        PetServiceResponseDTO responseDTO = new PetServiceResponseDTO(service.getById(id));
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Lista os serviços de pet", description = "Recupera uma lista paginada de serviços de pet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços de pet recuperada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<PetServiceResponseDTO>> list(
            @RequestParam(defaultValue = "0") @Parameter(description = "Número da página de resultados") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Número de itens por página") Integer size) {
        Page<PetServiceResponseDTO> petServices = service.list(page, size);
        return ResponseEntity.ok(petServices);
    }

    @Operation(summary = "Deleta um serviço de pet pelo ID", description = "Deleta um serviço de pet específico baseado no ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço de pet deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço de pet não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "ID do serviço de pet a ser deletado") String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
