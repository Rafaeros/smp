package br.rafaeros.smp.modules.device.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.device.model.UserDevice;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    @Query("SELECT ud FROM ud JOIN FETCH ud.device WHERE ud.user.id = :userId")
    List<UserDevice> findAllByUserId(Long userId);

    boolean exitsByUserIdAndDeviceId(Long userId, Long deviceId);

    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, Long deviceId);
}
