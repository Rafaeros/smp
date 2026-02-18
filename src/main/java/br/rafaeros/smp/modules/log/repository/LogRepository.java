package br.rafaeros.smp.modules.log.repository;

import java.time.Instant;
import java.util.List;

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
            COALESCE(SUM(l.quantityProduced), 0) AS quantityProduced,
            COALESCE(AVG(l.cycleTime + COALESCE(l.pausedTime, 0)), 0) AS avgCycleTime,
            COALESCE(MIN(l.cycleTime + COALESCE(l.pausedTime, 0)), 0) AS minCycleTime,
            COALESCE(MAX(l.cycleTime + COALESCE(l.pausedTime, 0)), 0) AS maxCycleTime
        FROM Log l
        WHERE l.createdAt BETWEEN :startDate AND :endDate
    """)
    IOrderStats getGlobalStatsByRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("""
                SELECT
                    COUNT(l) AS totalLogs,
                    AVG(l.cycleTime + COALESCE(l.pausedTime, 0)) AS avgCycleTime,
                    MIN(l.cycleTime + COALESCE(l.pausedTime, 0)) AS minCycleTime,
                    MAX(l.cycleTime + COALESCE(l.pausedTime, 0)) AS maxCycleTime,
                    SUM(l.quantityProduced) AS quantityProduced,
                    SUM(COALESCE(l.quantityPaused, 0)) AS totalPauses
                FROM Log l
                WHERE l.order.id = :orderId
            """)
    IOrderStats getStatsByOrder(@Param("orderId") Long orderId);

    @Query("""
        SELECT new br.rafaeros.smp.modules.log.controller.dto.ProductStatsDTO(
            p.code,
            p.description,
            COUNT(l),
            AVG(l.cycleTime + COALESCE(l.pausedTime, 0)),
            MIN(l.cycleTime + COALESCE(l.pausedTime, 0)),
            MAX(l.cycleTime + COALESCE(l.pausedTime, 0))
        )
        FROM Product p
        LEFT JOIN Order o ON o.product = p
        LEFT JOIN o.logs l
        WHERE p.id = :productId
        GROUP BY p.code, p.description
    """)
    ProductStatsDTO getStatsByProduct(@Param("productId") Long productId);

    @Query("SELECT l FROM Log l JOIN FETCH l.order JOIN FETCH l.device ORDER BY l.createdAt DESC")
    List<Log> findRecentLogs(Pageable pageable);
}