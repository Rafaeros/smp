package br.rafaeros.smp.modules.userdevice.model;

import br.rafaeros.smp.core.model.BaseEntity;
import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_devices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "device_id"})
})
public class UserDevice extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "device_id")
    private Device device;

    @Column(name = "custom_name")
    private String name;

    @Column(name = "pos_x")
    private Double coordinateX;

    @Column(name = "pos_y")
    private Double coordinateY;

    public UserDevice(User user, Device device, String name) {
        this.user = user;
        this.device = device;
        this.name = name;
    }
}
