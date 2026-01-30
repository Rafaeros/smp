package br.rafaeros.smp.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.userdevice.repository.UserDeviceRepository;

@Component("deviceSecurity")
public class DeviceSecurity {

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    public boolean canAccess(Long deviceId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean isAdminOrManager = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        if (isAdminOrManager) {
            return true;
        }

        return userDeviceRepository.existsByUserIdAndDeviceId(user.getId(), deviceId);
    }
    
    public boolean canAccessByMac(String macAddress, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        boolean isAdminOrManager = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        if (isAdminOrManager) return true;

        return userDeviceRepository.existsByUserIdAndDeviceMacAddress(user.getId(), macAddress);
    }
}