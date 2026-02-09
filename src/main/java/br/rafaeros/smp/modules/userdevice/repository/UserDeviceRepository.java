package br.rafaeros.smp.modules.userdevice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.userdevice.model.UserDevice;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    @Query("SELECT ud FROM UserDevice ud JOIN FETCH ud.device WHERE ud.user.id = :userId")
    List<UserDevice> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ud FROM UserDevice ud JOIN FETCH ud.device WHERE ud.user.id = :userId AND " +
            "(:name IS NULL OR LOWER(CAST(ud.name AS string)) LIKE LOWER(CAST(:name AS string))) AND " +
            "(:macAddress IS NULL OR LOWER(CAST(ud.device.macAddress AS string)) LIKE LOWER(CAST(:macAddress AS string))) AND "
            +
            "(:status IS NULL OR ud.device.status = :status)")
    List<UserDevice> findByUserIdWithFilter(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("macAddress") String macAddress,
            @Param("status") DeviceStatus status,
            Sort sort);

    boolean existsByDeviceId(Long deviceId);

    boolean existsByUserIdAndDeviceId(Long userId, Long deviceId);

    boolean existsByUserIdAndDeviceMacAddress(Long userId, String macAddress);

    Optional<UserDevice> findByIdAndUserId(Long id, Long userId);

    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, Long deviceId);
}