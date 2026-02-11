package br.rafaeros.smp.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.model.enums.Role;
import br.rafaeros.smp.modules.userdevice.repository.UserDeviceRepository;

@Component("deviceSecurity")
public class DeviceSecurity {

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    public boolean canAccess(Long userDeviceId, User user) {
        if (user == null) return false;

        boolean isAdminOrManager = user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER;

        if (isAdminOrManager) {
            return true;
        }
        return userDeviceRepository.existsByIdAndUserId(userDeviceId, user.getId());
    }
}