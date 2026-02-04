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
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.modules.client.controller.dto.ClientResponseDTO;
import br.rafaeros.smp.modules.client.controller.dto.CreateClientDTO;
import br.rafaeros.smp.modules.client.service.ClientService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@RequestBody @Valid CreateClientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getAll(
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        return ResponseEntity.ok(clientService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ClientResponseDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(clientService.findByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @RequestBody @Valid CreateClientDTO dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

}
