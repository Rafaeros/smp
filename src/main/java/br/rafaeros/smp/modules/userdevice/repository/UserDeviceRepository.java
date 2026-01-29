package br.rafaeros.smp.modules.userdevice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.userdevice.model.UserDevice;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    @Query("SELECT ud FROM UserDevice ud JOIN FETCH ud.device WHERE ud.user.id = :userId")
    List<UserDevice> findAllByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndDeviceId(Long userId, Long deviceId);

    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, Long deviceId);
}
