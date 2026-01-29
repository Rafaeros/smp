package br.rafaeros.smp.modules.userdevice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.modules.device.controller.dto.UserDeviceResponseDTO;
import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.userdevice.controller.dto.DeviceBindingDTO;
import br.rafaeros.smp.modules.userdevice.service.UserDeviceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user-devices")
public class UserDeviceController {

    @Autowired
    private UserDeviceService userDeviceService;

    @PostMapping("/bind")
    public ResponseEntity<UserDeviceResponseDTO> bindDevice(
            @RequestBody @Valid DeviceBindingDTO dto,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        
        return ResponseEntity.ok(userDeviceService.linkDeviceToUser(user.getId(), dto));
    }
}