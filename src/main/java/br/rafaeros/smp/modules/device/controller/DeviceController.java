package br.rafaeros.smp.modules.device.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.modules.device.controller.dto.CreateDeviceRequestDTO;
import br.rafaeros.smp.modules.device.controller.dto.DeviceDetailsResponseDTO;
import br.rafaeros.smp.modules.device.controller.dto.DeviceResponseDTO;
import br.rafaeros.smp.modules.device.controller.dto.UpdateDeviceDTO;
import br.rafaeros.smp.modules.device.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getAllAvailableDevices() {
        return ResponseEntity.ok(ApiResponse.success("Dispositivos disponíveis listados com sucesso.",
                deviceService.findAllAvailableDevices()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> createDevice(@RequestBody @Valid CreateDeviceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dispositivo criado com sucesso!", deviceService.createDevice(dto)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getAllDevices() {
        return ResponseEntity.ok(ApiResponse.success("Dispositivos listados com sucesso.", deviceService.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canAccess(#id, authentication)")
    public ResponseEntity<ApiResponse<DeviceDetailsResponseDTO>> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Dispositivo encontrado.", deviceService.findById(id)));
    }

    @GetMapping("/mac/{macAddress}")
    @PreAuthorize("@deviceSecurity.canAccessByMac(#macAddress, authentication)")
    public ResponseEntity<ApiResponse<DeviceDetailsResponseDTO>> getDeviceByMacAddress(
            @PathVariable String macAddress) {
        return ResponseEntity
                .ok(ApiResponse.success("Dispositivo encontrado.", deviceService.findByMacAddress(macAddress)));
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("@deviceSecurity.canAccess(#id, authentication)")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> updateDeviceById(@PathVariable Long id,
            @RequestBody @Valid UpdateDeviceDTO dto) {
        return ResponseEntity
                .ok(ApiResponse.success("Dispositivo atualizado com sucesso!", deviceService.updateById(id, dto)));
    }

    @PatchMapping("/update/mac/{macAddress}")
    @PreAuthorize("@deviceSecurity.canAccessByMac(#macAddress, authentication)")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> updateDeviceByMacAddress(@PathVariable String macAddress,
            @RequestBody @Valid UpdateDeviceDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Dispositivo atualizado com sucesso!",
                deviceService.updateByMacAddress(macAddress, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDeviceById(@PathVariable Long id) {
        deviceService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Dispositivo removido com sucesso!"));
    }

}
