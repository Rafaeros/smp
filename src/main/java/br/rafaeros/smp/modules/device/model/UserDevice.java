package br.rafaeros.smp.modules.device.model;

import br.rafaeros.smp.core.model.BaseEntity;
import br.rafaeros.smp.modules.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_devices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDevice extends BaseEntity{
    
    @EmbeddedId
    private UserDeviceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name= "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("deviceId")
    @JoinColumn(name= "device_id")
    private Device device;

    @Column(name = "custom_name")
    private String name;

    public UserDevice(Long userId, Long deviceId, String name) {
        this.id = new UserDeviceId(userId, deviceId);
        this.name = name;
    }

}
