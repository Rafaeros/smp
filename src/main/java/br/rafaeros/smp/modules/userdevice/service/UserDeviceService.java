package br.rafaeros.smp.modules.userdevice.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.repository.DeviceRepository;
import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.repository.UserRepository;
import br.rafaeros.smp.modules.userdevice.controller.dto.DeviceBindingDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UpdateDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceMapResponseDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.model.UserDevice;
import br.rafaeros.smp.modules.userdevice.repository.UserDeviceRepository;

@Service
public class UserDeviceService {
    @Autowired
    private UserDeviceRepository userDeviceRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserDeviceDetailsDTO linkDeviceToUser(Long userId, DeviceBindingDTO dto) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Device device = deviceRepository.findById(Objects.requireNonNull(dto.id()))
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo não encontrado"));

        UserDevice userDevice = new UserDevice();
        userDevice.setUser(user);
        userDevice.setDevice(device);
        userDevice.setName(dto.name());
        userDevice.setCoordinateX(dto.coordinateX());
        userDevice.setCoordinateY(dto.coordinateY());

        UserDevice saved = userDeviceRepository.save(userDevice);
        return UserDeviceDetailsDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<UserDeviceMapResponseDTO> getMyMap(Long userId) {
        return userDeviceRepository.findByUserId(userId)
                .stream()
                .map(UserDeviceMapResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDeviceDetailsDTO> getMyDevices(Long userId, Long deviceId) {
        return userDeviceRepository.findByUserId(userId)
                .stream()
                .map(UserDeviceDetailsDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDeviceDetailsDTO findById(Long id, Long userId) {
        UserDevice userDevice = userDeviceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo do usuário não encontrado"));

        return UserDeviceDetailsDTO.fromEntity(userDevice);
    }

    @Transactional
    public UserDeviceDetailsDTO updateDeviceDetails(Long userDeviceId, Long userId, UpdateDeviceDetailsDTO dto) {
        UserDevice userDevice = userDeviceRepository.findByIdAndUserId(userDeviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo do usuário não encontrado"));

        if (userDevice.getDevice() == null) {
            throw new ResourceNotFoundException("Dispositivo associado não encontrado");
        }

        if (dto.name() != null) {
            userDevice.setName(dto.name());
        }

        if (dto.processStage() != null) {
            userDevice.getDevice().setCurrentStage(dto.processStage());
        }

        UserDevice updated = userDeviceRepository.save(userDevice);
        return UserDeviceDetailsDTO.fromEntity(updated);

    }

}
