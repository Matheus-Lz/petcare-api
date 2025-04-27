package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.employee.CreateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.employee.UpdateEmployeeRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @ApiResponse(responseCode = "200", description = "Funcionário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody @Valid CreateEmployeeRequestDTO requestDTO) {
        Employee created = service.create(requestDTO);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Atualiza os serviços de um funcionário", description = "Atualiza os serviços vinculados a um funcionário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable @Parameter(description = "ID do funcionário") String id,
                                           @RequestBody @Valid UpdateEmployeeRequestDTO requestDTO) {
        Employee updated = service.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Busca um funcionário por ID", description = "Recupera os dados de um funcionário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário encontrado"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable @Parameter(description = "ID do funcionário") String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Lista todos os funcionários", description = "Retorna uma lista de todos os funcionários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionários listados com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<Employee>> list() {
        return ResponseEntity.ok(service.list());
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
