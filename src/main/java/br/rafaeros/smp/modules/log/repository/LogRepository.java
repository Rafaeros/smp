package br.rafaeros.smp.modules.log.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.log.controller.dto.IOrderStats;
import br.rafaeros.smp.modules.log.controller.dto.ProductStatsDTO;
import br.rafaeros.smp.modules.log.model.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    Page<Log> findByOrderId(Long orderId, Pageable pageable);

    Page<Log> findByOrder_Product_Id(Long productId, Pageable pageable);

    @Query("""
                SELECT
                    COUNT(l) AS totalLogs,
                    AVG(l.cycleTime) AS avgCycleTime,
                    MIN(l.cycleTime) AS minCycleTime,
                    MAX(l.cycleTime) AS maxCycleTime,
                    SUM(l.quantityProduced) AS quantityProduced
                FROM Log l
                WHERE l.order.id = :orderId
            """)
    IOrderStats getStatsByOrder(@Param("orderId") Long orderId);

    @Query("""
        SELECT new br.rafaeros.smp.modules.log.controller.dto.ProductStatsDTO(
            p.code,
            p.description,
            COUNT(l),
            AVG(l.cycleTime),
            MIN(l.cycleTime),
            MAX(l.cycleTime)
        )
        FROM Product p
        LEFT JOIN Order o ON o.product = p
        LEFT JOIN o.logs l
        WHERE p.id = :productId
        GROUP BY p.code, p.description
    """)
    ProductStatsDTO getStatsByProduct(@Param("productId") Long productId);
}