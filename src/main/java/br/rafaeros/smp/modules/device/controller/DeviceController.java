package br.rafaeros.smp.modules.device.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.rafaeros.smp.modules.device.controller.dto.CreateDeviceRequestDTO;
import br.rafaeros.smp.modules.device.controller.dto.DeviceDetailsResponseDTO;
import br.rafaeros.smp.modules.device.controller.dto.DeviceResponseDTO;
import br.rafaeros.smp.modules.device.controller.dto.UpdateDeviceDTO;
import br.rafaeros.smp.modules.device.service.DeviceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponseDTO> createDevice(@RequestBody @Valid CreateDeviceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.createDevice(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeviceResponseDTO>> getAllDevices() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @GetMapping("/available")
    public ResponseEntity<List<DeviceResponseDTO>> getAllAvailableDevices() {
        return ResponseEntity.ok(deviceService.findAvailableDevices());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canAccess(#id, authentication)")
    public ResponseEntity<DeviceDetailsResponseDTO> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.findById(id));
    }

    @GetMapping("/mac/{macAddress}")
    @PreAuthorize("@deviceSecurity.canAccessByMac(#macAddress, authentication)")
    public ResponseEntity<DeviceDetailsResponseDTO> getDeviceByMacAddress(@PathVariable String macAddress) {
        return ResponseEntity.ok(deviceService.findByMacAddress(macAddress));
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("@deviceSecurity.canAccess(#id, authentication)")
    public ResponseEntity<DeviceResponseDTO> updateDeviceById(@PathVariable Long id, @RequestBody @Valid UpdateDeviceDTO dto) {
        return ResponseEntity.ok(deviceService.updateById(id, dto));
    }

    @PatchMapping("/update/mac/{macAddress}")
    @PreAuthorize("@deviceSecurity.canAccessByMac(#macAddress, authentication)")
    public ResponseEntity<DeviceResponseDTO> updateDeviceByMacAddress(@PathVariable String macAddress, @RequestBody @Valid UpdateDeviceDTO dto) {
        return ResponseEntity.ok(deviceService.updateByMacAddress(macAddress, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDeviceById(@PathVariable Long id) {
        deviceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
