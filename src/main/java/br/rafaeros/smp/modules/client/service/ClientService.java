package br.rafaeros.smp.modules.client.service;

import java.util.Objects;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.client.controller.dto.ClientResponseDTO;
import br.rafaeros.smp.modules.client.controller.dto.CreateClientDTO;
import br.rafaeros.smp.modules.client.model.Client;
import br.rafaeros.smp.modules.client.repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientResponseDTO createClient(CreateClientDTO dto) {
        Client client = new Client();
        client.setName(dto.name());
        clientRepository.save(client);
        return ClientResponseDTO.fromEntity(client);
    }

    public Page<ClientResponseDTO> findAll(Pageable pageable, String name) {
        Pageable safePage = Objects.requireNonNull(pageable);

        String nameFilter = (name != null && !name.isBlank()) ? "%" + name + "%" : null;

        if (name == null) {
            return clientRepository.findAll(safePage).map(ClientResponseDTO::fromEntity);
        }
        return clientRepository.findByFilters(nameFilter, safePage).map(ClientResponseDTO::fromEntity);
    }

    @Cacheable("clients")
    public Client findByNameOrCreate(String name) {
        return clientRepository.findByName(name)
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setName(name);
                    return clientRepository.save(newClient);
                });
    }

    public ClientResponseDTO findById(Long id) {
        return ClientResponseDTO.fromEntity(findByIdInternal(id));
    }

    public ClientResponseDTO findByName(String name) {
        return ClientResponseDTO.fromEntity(findByNameInternal(name));
    }

    public ClientResponseDTO updateClient(Long id, CreateClientDTO dto) {
        Client client = findByIdInternal(id);
        client.setName(dto.name());
        clientRepository.save(client);
        return ClientResponseDTO.fromEntity(client);
    }

    public void deleteClient(Long id) {
        Long safeId = Objects.requireNonNull(id);
        if (!clientRepository.existsById(safeId)) {
            throw new ResourceNotFoundException("Cliente nao encontrado com o ID: " + id);
        }

        try {
            clientRepository.deleteById(safeId);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                    "Não é possível excluir este cliente pois ele já possui Ordens de Produção vinculadas.", e);
        }
    }

    public Client findByIdInternal(Long id) {
        Long safeId = Objects.requireNonNull(id);
        return clientRepository.findById(safeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + safeId));
    }

    private Client findByNameInternal(String name) {
        return clientRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o nome: " + name));
    }
}
