package br.rafaeros.smp.modules.userdevice.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.BusinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.repository.DeviceRepository;
import br.rafaeros.smp.modules.order.model.Order;
import br.rafaeros.smp.modules.order.repository.OrderRepository;
import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.model.enums.Role;
import br.rafaeros.smp.modules.user.repository.UserRepository;
import br.rafaeros.smp.modules.userdevice.controller.dto.DeviceBindingDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UpdateDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceMapResponseDTO;
import br.rafaeros.smp.modules.userdevice.model.UserDevice;
import br.rafaeros.smp.modules.userdevice.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeviceService {
    private final UserDeviceRepository userDeviceRepository;
    private final DeviceRepository deviceRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserDeviceDetailsDTO bindDeviceToUser(Long userId, DeviceBindingDTO dto) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Device device = deviceRepository.findById(Objects.requireNonNull(dto.id()))
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo não encontrado"));

        if (userDeviceRepository.existsByUserIdAndDeviceId(userId, dto.id())) {
            throw new BusinessException("Este dispositivo já está vinculado ao seu usuário.");
        }

        if (userDeviceRepository.existsByDeviceId(device.getId())) {
            throw new BusinessException("Este dispositivo já está vinculado a um usuário.");
        }

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
    public List<UserDeviceMapResponseDTO> getMyMap(User user) {

        Long userId = user.getId();

        if (user.getRole() == Role.ADMIN) {
            return userDeviceRepository.findAll()
                    .stream()
                    .map(UserDeviceMapResponseDTO::fromEntity)
                    .toList();
        }
        return userDeviceRepository.findByUserId(userId)
                .stream()
                .map(UserDeviceMapResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDeviceMapResponseDTO> getMyDevices(Pageable pageable, Long userId, String name, String macAddress,
            String status) {
        Sort sort = pageable != null ? pageable.getSort() : Sort.by("name");
        String nameFilter = (name != null && !name.isBlank()) ? "%" + name + "%" : null;
        String macAddressFilter = (macAddress != null && !macAddress.isBlank()) ? "%" + macAddress + "%" : null;
        DeviceStatus deviceStatus = (status != null && !status.isBlank()) ? DeviceStatus.valueOf(status.toUpperCase())
                : null;

        boolean hasFilter = nameFilter != null || macAddressFilter != null || deviceStatus != null;

        List<UserDevice> devices;

        if (hasFilter) {
            devices = userDeviceRepository.findByUserIdWithFilter(
                    userId,
                    nameFilter,
                    macAddressFilter,
                    deviceStatus,
                    sort);
        } else {
            devices = userDeviceRepository.findByUserId(userId);
        }

        return devices.stream()
                .map(UserDeviceMapResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDeviceDetailsDTO findById(Long id, Long userId) {
        UserDevice userDevice = userDeviceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo do usuário não encontrado"));

        return UserDeviceDetailsDTO.fromEntity(userDevice);
    }

    @Transactional
    // Note que removemos o ID do DTO, usamos apenas o userDeviceId que vem da URL
    public UserDeviceDetailsDTO updateDeviceDetails(Long userDeviceId, Long userId, UpdateDeviceDetailsDTO dto) {
        UserDevice userDevice = userDeviceRepository.findByIdAndUserId(userDeviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo do usuário não encontrado"));

        if (userDevice.getDevice() == null) {
            throw new ResourceNotFoundException("Dispositivo físico associado não encontrado");
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            userDevice.setName(dto.name());
        }
        if (dto.processStage() != null) {
            userDevice.getDevice().setCurrentStage(dto.processStage());
        }

        if (dto.orderId() != null) {
            Long safeId = Objects.requireNonNull(dto.orderId());
            Order order = orderRepository.findById(safeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ordem de Produção não encontrada"));

            userDevice.getDevice().setCurrentOrder(order);
        }

        UserDevice updated = userDeviceRepository.save(userDevice);
        return UserDeviceDetailsDTO.fromEntity(updated);
    }

    @Transactional
    public void unbindDevice(Long userDeviceId, Long userId) {
        UserDevice userDevice = userDeviceRepository.findByIdAndUserId(userDeviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo do usuário não encontrado"));

        if (userDevice.getDevice() == null) {
            throw new ResourceNotFoundException("Dispositivo associado não encontrado");
        }

        userDeviceRepository.delete(userDevice);
    }
}