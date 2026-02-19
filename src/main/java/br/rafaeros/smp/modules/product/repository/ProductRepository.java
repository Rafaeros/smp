package br.rafaeros.smp.modules.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.product.controller.dto.ProductExportDTO;
import br.rafaeros.smp.modules.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        @Query("SELECT p FROM Product p WHERE " +
                        "(:code IS NULL OR LOWER(CAST(p.code AS string)) LIKE LOWER(CAST(:code AS string))) AND " +
                        "(:description IS NULL OR LOWER(CAST(p.description AS string)) LIKE LOWER(CAST(:description AS string)))")
        Page<Product> findByFilters(
                        @Param("code") String code,
                        @Param("description") String description,
                        Pageable pageable);

        @Query("SELECT p FROM Product p WHERE " +
                        "LOWER(CAST(p.code AS string)) LIKE LOWER(:query) OR " +
                        "LOWER(CAST(p.description AS string)) LIKE LOWER(:query)")
        Page<Product> searchByTerm(@Param("query") String query, Pageable pageable);

        @Query("""
                SELECT new br.rafaeros.smp.modules.product.controller.dto.ProductExportDTO(
                        p.code,
                        p.description,
                        COUNT(l.id),
                        AVG(l.cycleTime + COALESCE(l.pausedTime, 0.0)),
                        MIN(l.cycleTime + COALESCE(l.pausedTime, 0.0)),
                        MAX(l.cycleTime + COALESCE(l.pausedTime, 0.0))
                )
                FROM Product p
                LEFT JOIN Order o ON o.product = p
                LEFT JOIN Log l ON l.order = o
                GROUP BY p.code, p.description
                        """)
        List<ProductExportDTO> getProductExportStats();

        Optional<Product> findByCode(String code);

        boolean existsByCode(String code);
}
