package br.rafaeros.smp.modules.userdevice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.userdevice.model.UserDevice;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

        @Query(value = "SELECT ud FROM UserDevice ud JOIN FETCH ud.device d WHERE ud.user.id = :userId AND " +
                        "(:name IS NULL OR LOWER(ud.name) LIKE :name) AND " +
                        "(:macAddress IS NULL OR LOWER(CAST(d.macAddress AS string)) LIKE :macAddress) AND " +
                        "(:status IS NULL OR d.status = :status)",

                        countQuery = "SELECT count(ud) FROM UserDevice ud JOIN ud.device d WHERE ud.user.id = :userId AND "
                                        +
                                        "(:name IS NULL OR LOWER(ud.name) LIKE :name) AND " +
                                        "(:macAddress IS NULL OR LOWER(CAST(d.macAddress AS string)) LIKE :macAddress) AND "
                                        +
                                        "(:status IS NULL OR d.status = :status)")
        Page<UserDevice> findByUserIdAndFilters(
                        @Param("userId") Long userId,
                        @Param("name") String name,
                        @Param("macAddress") String macAddress,
                        @Param("status") DeviceStatus status,
                        Pageable pageable);

        @Query(value = "SELECT ud FROM UserDevice ud JOIN FETCH ud.device d WHERE " +
                        "(:name IS NULL OR LOWER(ud.name) LIKE :name) AND " +
                        "(:macAddress IS NULL OR LOWER(CAST(d.macAddress AS string)) LIKE :macAddress) AND " +
                        "(:status IS NULL OR d.status = :status)",

                        countQuery = "SELECT count(ud) FROM UserDevice ud JOIN ud.device d WHERE " +
                                        "(:name IS NULL OR LOWER(ud.name) LIKE :name) AND " +
                                        "(:macAddress IS NULL OR LOWER(CAST(d.macAddress AS string)) LIKE :macAddress) AND "
                                        +
                                        "(:status IS NULL OR d.status = :status)")
        Page<UserDevice> findAllWithFilters(
                        @Param("name") String name,
                        @Param("macAddress") String macAddress,
                        @Param("status") DeviceStatus status,
                        Pageable pageable);

        @Query("SELECT ud FROM UserDevice ud JOIN FETCH ud.device WHERE ud.user.id = :userId")
        List<UserDevice> findAllByUserIdForMap(@Param("userId") Long userId);

        @Query("SELECT ud FROM UserDevice ud JOIN FETCH ud.device")
        List<UserDevice> findAllForMap();

        Page<UserDevice> findAllByUserId(Long userId, Pageable pageable);

        List<UserDevice> findAllDevicesListByUserId(Long userId);

        boolean existsByIdAndUserId(Long id, Long userId);

        boolean existsByDeviceId(Long deviceId);

        boolean existsByUserIdAndDeviceId(Long userId, Long deviceId);

        boolean existsByUserIdAndDeviceMacAddress(Long userId, String macAddress);

        Optional<UserDevice> findByIdAndUserId(Long id, Long userId);

        Optional<UserDevice> findByUserIdAndDeviceId(Long userId, Long deviceId);
}