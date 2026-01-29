package br.rafaeros.smp.modules.device.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.device.model.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMacAddress(String macAddress);
    @Query("SELECT d FROM Device d WHERE NOT EXISTS (SELECT ud FROM UserDevice ud WHERE ud.device = d)")
    List<Device> findAllAvailable();

    boolean existsByMacAddress(String macAddress);
}