package br.rafaeros.smp.modules.userdevice.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.userdevice.controller.dto.DeviceBindingDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UpdateDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceDetailsDTO;
import br.rafaeros.smp.modules.userdevice.controller.dto.UserDeviceMapResponseDTO;
import br.rafaeros.smp.modules.userdevice.service.UserDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-devices")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDeviceDetailsDTO>> bindDevice(
            @RequestBody @Valid DeviceBindingDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Dispositivo vinculado com sucesso!",
                userDeviceService.bindDeviceToUser(user.getId(), dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDeviceMapResponseDTO>>> getMyDevices(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String macAddress,
            @RequestParam(required = false) String status) {

        Page<UserDeviceMapResponseDTO> pageResult = userDeviceService.getMyDevices(
                user,
                name,
                macAddress,
                status,
                pageable);

        return ResponseEntity.ok(ApiResponse.success("Dispositivos listados com sucesso!", pageResult));
    }

    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<UserDeviceMapResponseDTO>>> getMyMap(
            @AuthenticationPrincipal User user) {

        List<UserDeviceMapResponseDTO> listResult = userDeviceService.getMyMap(user);

        return ResponseEntity.ok(ApiResponse.success("Mapa de dispositivos carregado com sucesso!", listResult));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canAccessDevice(#id, principal)")
    public ResponseEntity<ApiResponse<UserDeviceDetailsDTO>> getDeviceDetails(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponse.success("Dispositivo Listado com sucesso!", userDeviceService.findById(id, user)));
    }

    @GetMapping("/{id}/devices")
    @PreAuthorize("@deviceSecurity.canAccessUserDevices(#id, principal)")
    public ResponseEntity<ApiResponse<Page<UserDeviceDetailsDTO>>> getAllDevicesByUserId(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success("Dispositivos listados com sucesso!",
                userDeviceService.findAllByUserId(id, user, pageable)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canAccessDevice(#id, principal)")
    public ResponseEntity<ApiResponse<UserDeviceDetailsDTO>> updateDeviceDetails(
            @PathVariable Long id,
            @RequestBody UpdateDeviceDetailsDTO dto,
            @AuthenticationPrincipal User user) {
        UserDeviceDetailsDTO userDevice = userDeviceService.updateDeviceDetails(id, user, dto);
        return ResponseEntity.ok(ApiResponse.success("Dispositivo atualizado com sucesso!", userDevice));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canAccessDevice(#id, principal)")
    public ResponseEntity<ApiResponse<Void>> deleteDevice(@PathVariable Long id, @AuthenticationPrincipal User user) {
        userDeviceService.unbindDevice(id, user);
        return ResponseEntity.ok(ApiResponse.success("Dispositivo removido com sucesso!"));
    }

}