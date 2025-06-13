package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.employee.CreateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.employee.EmployeeResponseDTO;
import com.petcare.petcare_api.application.dto.employee.UpdateEmployeeRequestDTO;
import com.petcare.petcare_api.coredomain.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    @Autowired
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @Operation(summary = "Cria um novo funcionário", description = "Cria um novo funcionário junto com o usuário vinculado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso"), // Corrigir aqui também para 201
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(@RequestBody @Valid CreateEmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO(service.create(requestDTO));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }


    @Operation(summary = "Atualiza os serviços de um funcionário", description = "Atualiza os serviços vinculados a um funcionário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable @Parameter(description = "ID do funcionário") String id,
                                                      @RequestBody @Valid UpdateEmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO(service.update(id, requestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Busca um funcionário por ID", description = "Recupera os dados de um funcionário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário encontrado"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable @Parameter(description = "ID do funcionário") String id) {
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO(service.getById(id));
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Lista todos os funcionários", description = "Retorna uma lista de todos os funcionários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionários listados com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDTO>> list(@RequestParam(defaultValue = "0") @Parameter(description = "Número da página de resultados") Integer page,
                                                          @RequestParam(defaultValue = "10") @Parameter(description = "Número de itens por página") Integer size) {
        Page<EmployeeResponseDTO> responseDTO = service.list(page, size).map(EmployeeResponseDTO::new);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Deleta um funcionário", description = "Remove logicamente um funcionário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Funcionário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "ID do funcionário") String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
