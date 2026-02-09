package br.rafaeros.smp.modules.orderlog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.orderlog.controller.dto.OrderStatsDTO;
import br.rafaeros.smp.modules.orderlog.controller.dto.ProductStatsDTO;
import br.rafaeros.smp.modules.orderlog.model.OrderLog;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    Page<OrderLog> findByOrderId(Pageable Pageable, Long orderId);
    Page<OrderLog> findByOrder_Product_Id(Long productId, Pageable pageable);
    @Query("""
        SELECT new br.rafaeros.smp.modules.orderlog.dto.OrderStatusDTO(
            COUNT(l),
            AVG(l.cycleTime),
            MIN(l.cycleTime),
            MAX(l.cycleTime),
            SUM(l.quantityProduced)
        )
        FROM OrderLog l
        WHERE l.order.id = :orderId
    """)
    OrderStatsDTO getStatsByOrder(@Param("orderId") Long orderId);

    // Estatísticas do PRODUTO (Soma de todas as OPs daquele produto)
    @Query("""
        SELECT new br.rafaeros.smp.modules.orderlog.dto.ProductStatsDTO(
            p.name,
            COUNT(l),
            AVG(l.cycleTime),
            MIN(l.cycleTime),
            MAX(l.cycleTime)
        )
        FROM OrderLog l
        JOIN l.order o
        JOIN o.product p
        WHERE p.id = :productId
        GROUP BY p.name
    """)
    ProductStatsDTO getStatsByProduct(@Param("productId") Long productId);
}