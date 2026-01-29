package br.rafaeros.smp.modules.userdevice.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.device.controller.dto.UserDeviceResponseDTO;
import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.repository.DeviceRepository;
import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.repository.UserRepository;
import br.rafaeros.smp.modules.userdevice.controller.dto.DeviceBindingDTO;
import br.rafaeros.smp.modules.userdevice.model.UserDevice;
import br.rafaeros.smp.modules.userdevice.repository.UserDeviceRepository;

@Service
public class UserDeviceService {
    @Autowired private UserDeviceRepository userDeviceRepository;
    @Autowired private DeviceRepository deviceRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public UserDeviceResponseDTO linkDeviceToUser(Long userId, DeviceBindingDTO dto) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Device device = deviceRepository.findById(Objects.requireNonNull(dto.id()))
            .orElseThrow(() -> new ResourceNotFoundException("Dispositivo nao encontrado"));

        UserDevice userDevice = new UserDevice(user.getId(), device.getId(), dto.name());
        
        userDeviceRepository.save(userDevice);

        return UserDeviceResponseDTO.fromEntity(device, dto.name());
    }

}
