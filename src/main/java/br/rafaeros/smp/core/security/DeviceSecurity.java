package br.rafaeros.smp.core.security;

import org.springframework.stereotype.Component;

import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.model.enums.Role;
import br.rafaeros.smp.modules.userdevice.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;

@Component("deviceSecurity")
@RequiredArgsConstructor
public class DeviceSecurity {

    private UserDeviceRepository userDeviceRepository;

    public boolean canAccessDevice(Long userDeviceId, User user) {
        if (user == null) return false;

        boolean isAdminOrManager =
                user.getRole() == Role.ADMIN ||
                user.getRole() == Role.MANAGER;

        if (isAdminOrManager) {
            return true;
        }

        return userDeviceRepository
                .existsByIdAndUserId(userDeviceId, user.getId());
    }

    public boolean canAccessUserDevices(Long userId, User user) {
        if (user == null) return false;

        boolean isAdminOrManager =
                user.getRole() == Role.ADMIN ||
                user.getRole() == Role.MANAGER;

        if (isAdminOrManager) {
            return true;
        }

        return userId.equals(user.getId());
    }
}
