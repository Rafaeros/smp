package br.rafaeros.smp.modules.device.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.modules.device.controller.dto.DeviceResponseDTO;
import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;
import br.rafaeros.smp.modules.device.repository.DeviceRepository;
import jakarta.transaction.Transactional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public DeviceResponseDTO registerOrUpdateDevice(String macAddress, String ipAddress) {
        return deviceRepository.findByMacAddress(macAddress)
                .map(existingDevice -> {
                    existingDevice.setIpAddress(ipAddress);
                    existingDevice.setStatus(DeviceStatus.ONLINE);
                    existingDevice.setLastSeen(Instant.now());
                    return DeviceResponseDTO.fromEntity(deviceRepository.save(existingDevice));
                })
                .orElseGet(() -> {
                    Device newDevice = new Device();
                    newDevice.setMacAddress(macAddress);
                    newDevice.setIpAddress(ipAddress);
                    newDevice.setStatus(DeviceStatus.ONLINE);
                    newDevice.setProcess(ProcessStatus.IDLE);
                    newDevice.setLastSeen(Instant.now());
                    return DeviceResponseDTO.fromEntity(deviceRepository.save(newDevice));
                });
    }

    public void setStatusOffline(String macAddress) {
        deviceRepository.findByMacAddress(macAddress)
                .ifPresent(device -> {
                    device.setStatus(DeviceStatus.OFFLINE);
                    deviceRepository.save(device);
                });
    }
}
