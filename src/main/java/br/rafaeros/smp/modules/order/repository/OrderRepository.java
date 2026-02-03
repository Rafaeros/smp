package br.rafaeros.smp.modules.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCode(String code);
}
