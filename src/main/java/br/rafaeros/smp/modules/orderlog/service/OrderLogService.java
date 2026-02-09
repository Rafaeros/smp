package br.rafaeros.smp.modules.orderlog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.modules.orderlog.controller.dto.IOrderStats;
import br.rafaeros.smp.modules.orderlog.controller.dto.IProductStats;
import br.rafaeros.smp.modules.orderlog.controller.dto.OrderLogResponseDTO;
import br.rafaeros.smp.modules.orderlog.repository.OrderLogRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderLogService {

    private final OrderLogRepository repository;

    // --- FEATURE: List ---

    public Page<OrderLogResponseDTO> findLogsByOrder(Pageable pageable, Long orderId) {
        return repository.findByOrderId(orderId, pageable).map(OrderLogResponseDTO::fromEntity);
    }

    public Page<OrderLogResponseDTO> findLogsByProduct(Long productId, Pageable pageable) {
        return repository.findByOrder_Product_Id(productId, pageable).map(OrderLogResponseDTO::fromEntity);
    }

    // --- FEATURE: DASHBOARD/KPI ---

    public IOrderStats findOrderStats(Long orderId) {
        return repository.getStatsByOrder(orderId);
    }

    public IProductStats findProductStats(Long productId) {
        return repository.getStatsByProduct(productId);
    }
}