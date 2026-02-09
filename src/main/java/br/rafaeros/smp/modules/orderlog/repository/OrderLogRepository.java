package br.rafaeros.smp.modules.orderlog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Importe as interfaces
import br.rafaeros.smp.modules.orderlog.controller.dto.IOrderStats;
import br.rafaeros.smp.modules.orderlog.controller.dto.IProductStats;
import br.rafaeros.smp.modules.orderlog.model.OrderLog;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {

    Page<OrderLog> findByOrderId(Long orderId, Pageable pageable);

    Page<OrderLog> findByOrder_Product_Id(Long productId, Pageable pageable);

    @Query("""
                SELECT
                    COUNT(l) AS totalLogs,
                    AVG(l.cycleTime) AS avgCycleTime,
                    MIN(l.cycleTime) AS minCycleTime,
                    MAX(l.cycleTime) AS maxCycleTime,
                    SUM(l.quantityProduced) AS quantityProduced
                FROM OrderLog l
                WHERE l.order.id = :orderId
            """)
    IOrderStats getStatsByOrder(@Param("orderId") Long orderId);

    @Query("""
                SELECT
                    p.code AS code,
                    p.description AS name,
                    COUNT(l) AS totalLogs,
                    AVG(l.cycleTime) AS avgCycleTime,
                    MIN(l.cycleTime) AS minCycleTime,
                    MAX(l.cycleTime) AS maxCycleTime
                FROM OrderLog l
                JOIN l.order o
                JOIN o.product p
                WHERE p.id = :productId
                GROUP BY p.code, p.description
            """)
    IProductStats getStatsByProduct(@Param("productId") Long productId);
}