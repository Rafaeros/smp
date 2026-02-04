package br.rafaeros.smp.modules.userdevice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.userdevice.controller.dto.DeviceBindingDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UpdateDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceMapResponseDTO;
import br.rafaeros.smp.modules.userdevice.service.UserDeviceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user-devices")
public class UserDeviceController {

    @Autowired
    private UserDeviceService userDeviceService;

    @PostMapping("/bind")
    public ResponseEntity<UserDeviceDetailsDTO> bindDevice(
            @RequestBody @Valid DeviceBindingDTO dto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userDeviceService.bindDeviceToUser(user.getId(), dto));
    }

    @GetMapping("/my-map")
    public ResponseEntity<List<UserDeviceMapResponseDTO>> getMyMap(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userDeviceService.getMyMap(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canAccess(#id, authentication)")
    public ResponseEntity<UserDeviceDetailsDTO> getDetails(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userDeviceService.findById(id, user.getId()));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canAccess(#id, authentication)")
    public ResponseEntity<UserDeviceDetailsDTO> updateDeviceDetails(@PathVariable Long id,
            @RequestBody @Valid UpdateDeviceDetailsDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserDeviceDetailsDTO userDevice = userDeviceService.updateDeviceDetails(id, user.getId(), dto);
        return ResponseEntity.ok(userDevice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userDeviceService.unbindDevice(id, user.getId());
        return ResponseEntity.noContent().build();
    }

}