package br.rafaeros.smp.modules.device.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.device.controller.dto.DeviceDetailsResponseDTO;
import br.rafaeros.smp.modules.device.controller.dto.DeviceResponseDTO;
import br.rafaeros.smp.modules.device.controller.dto.UpdateDeviceDTO;
import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;
import br.rafaeros.smp.modules.device.repository.DeviceRepository;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public DeviceResponseDTO registerDevice(String macAddress, String ipAddress) {
        boolean exists = deviceRepository.existsByMacAddress(macAddress);
        if (exists) {
            throw new ResourceNotFoundException("Dispositivo com o MAC " + macAddress + " ja cadastrado.");
        }
        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setIpAddress(ipAddress);
        device.setStatus(DeviceStatus.ONLINE);
        device.setProcessStatus(ProcessStatus.IDLE);
        device.setLastSeen(Instant.now());
        return DeviceResponseDTO.fromEntity(deviceRepository.save(device));
    }

    @Transactional
    public DeviceResponseDTO registerOrUpdateDevice(String macAddress, String ipAddress) {
        return deviceRepository.findByMacAddress(macAddress)
                .map(existingDevice -> {
                    existingDevice.setIpAddress(ipAddress);
                    existingDevice.setStatus(DeviceStatus.ONLINE);
                    existingDevice.setProcessStatus(ProcessStatus.IDLE);
                    existingDevice.setLastSeen(Instant.now());
                    return DeviceResponseDTO.fromEntity(deviceRepository.save(existingDevice));
                })
                .orElseGet(() -> {
                    Device newDevice = new Device();
                    newDevice.setMacAddress(macAddress);
                    newDevice.setIpAddress(ipAddress);
                    newDevice.setStatus(DeviceStatus.ONLINE);
                    newDevice.setProcessStatus(ProcessStatus.IDLE);
                    newDevice.setLastSeen(Instant.now());
                    return DeviceResponseDTO.fromEntity(deviceRepository.save(newDevice));
                });
    }

    @Transactional(readOnly = true)
    public List<DeviceResponseDTO> findAll() {
        List<Device> devices = deviceRepository.findAll();
        return devices.stream().map(DeviceResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<DeviceResponseDTO> findAvailableDevices() {
        return deviceRepository.findAllAvailable()
                .stream()
                .map(DeviceResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceDetailsResponseDTO findById(Long id) {
        Device device = deviceRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo nao encontrado"));
        return DeviceDetailsResponseDTO.fromEntity(device);
    }

    @Transactional(readOnly = true)
    public DeviceDetailsResponseDTO findByMacAddress(String macAddress) {
        Device device = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo nao encontrado"));
        return DeviceDetailsResponseDTO.fromEntity(device);
    }

    @Transactional
    public DeviceResponseDTO updateById(Long id, UpdateDeviceDTO dto) {
        Device device = deviceRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo não encontrado"));

        device.setMacAddress(dto.macAddress());
        device.setIpAddress(dto.ipAddress());

        return DeviceResponseDTO.fromEntity(deviceRepository.save(device));
    }

    @Transactional
    public DeviceResponseDTO updateByMacAddress(String macAddress, UpdateDeviceDTO dto) {
        Device device = deviceRepository.findByMacAddress(dto.macAddress())
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo nao encontrado"));

        device.setMacAddress(dto.macAddress());
        device.setIpAddress(dto.ipAddress());

        return DeviceResponseDTO.fromEntity(deviceRepository.save(device));
    }

    @Transactional
    public DeviceResponseDTO updateProcessStatus(String macAddress, ProcessStatus processStatus) {
        Device device = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo nao encontrado"));

        device.setProcessStatus(processStatus);
        device.setLastSeen(Instant.now());
        return DeviceResponseDTO.fromEntity(deviceRepository.save(device));
    }

    @Transactional
    public void updateDeviceStatus(String macAddress, DeviceStatus status) {
        deviceRepository.findByMacAddress(macAddress)
                .ifPresent(device -> {
                    device.setStatus(status);
                    deviceRepository.save(device);
                });
    }

    @Transactional
    public void deleteById(Long id) {
        if (!existsById(id)) {
            throw new ResourceNotFoundException("Dispositivo nao encontrado");
        }
        deviceRepository.deleteById(Objects.requireNonNull(id));
    }

    @Transactional
    public void deleteByMacAddress(String macAddress) {
        Device device = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo nao encontrado"));

        if (device != null) {
            deviceRepository.delete(device);
        }
    }

    private boolean existsById(Long id) {
        Long safeId = Objects.requireNonNull(id);
        return deviceRepository.existsById(safeId);
    }
}
