package br.rafaeros.smp.modules.orderlog.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.order.model.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Long quantityProduced;

    private Double cycleTime;
    private Double pausedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    public Double getTotalTime() {
        return (cycleTime != null ? cycleTime : 0) + (pausedTime != null ? pausedTime : 0);
    }

}
