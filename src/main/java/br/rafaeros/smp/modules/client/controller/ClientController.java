package br.rafaeros.smp.modules.client.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.modules.client.controller.dto.ClientResponseDTO;
import br.rafaeros.smp.modules.client.controller.dto.CreateClientDTO;
import br.rafaeros.smp.modules.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponseDTO>> createClient(@RequestBody @Valid CreateClientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente criado com sucesso!", clientService.createClient(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClientResponseDTO>>> getAll(
            @PageableDefault(page = 0, size = 20) Pageable pageable, @RequestParam(required = false) String name) {
        return ResponseEntity
                .ok(ApiResponse.success("Clientes listados com sucesso.", clientService.findAll(pageable, name)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Cliente encontrado.", clientService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDTO>> updateClient(
            @PathVariable Long id,
            @RequestBody @Valid CreateClientDTO dto) {
        return ResponseEntity
                .ok(ApiResponse.success("Cliente atualizado com sucesso!", clientService.updateClient(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success("Cliente removido com sucesso!"));
    }

}
