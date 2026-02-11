package br.rafaeros.smp.modules.log.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.device.service.DeviceService;
import br.rafaeros.smp.modules.log.controller.dto.CreateLogRequestDTO;
import br.rafaeros.smp.modules.log.controller.dto.IOrderStats;
import br.rafaeros.smp.modules.log.controller.dto.LogResponseDTO;
import br.rafaeros.smp.modules.log.controller.dto.ProductStatsDTO;
import br.rafaeros.smp.modules.log.model.Log;
import br.rafaeros.smp.modules.log.repository.LogRepository;
import br.rafaeros.smp.modules.order.service.OrderService;
import br.rafaeros.smp.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

    private final OrderService orderService;
    private final DeviceService deviceService;

    private final LogRepository repository;
    private final ProductRepository productRepository;

    public LogResponseDTO create(CreateLogRequestDTO request) {
        if (request == null) {
            throw new ResourceNotFoundException("Requisição inválida");
        }

        orderService.findByIdInternal(request.orderId());
        deviceService.findByIdInternal(request.deviceId());

        Log log = new Log();
        log.setQuantityProduced(request.quantityProduced());
        log.setQuantityPaused(request.quantityPaused());
        log.setCycleTime(request.cycleTime());
        log.setPausedTime(request.pausedTime());
        log.setOrder(orderService.findByIdInternal(request.orderId()));
        log.setDevice(deviceService.findByIdInternal(request.deviceId()));

        Log saved = repository.save(log);
        return LogResponseDTO.fromEntity(saved);
    }   

    // --- FEATURE: List ---

    public Page<LogResponseDTO> findAll(Pageable pageable) {
        Pageable safePage = Objects.requireNonNull(pageable);
        return repository.findAll(safePage).map(LogResponseDTO::fromEntity);
    }

    public Page<LogResponseDTO> findLogsByOrder(Pageable pageable, Long orderId) {
        return repository.findByOrderId(orderId, pageable).map(LogResponseDTO::fromEntity);
    }

    public Page<LogResponseDTO> findLogsByProduct(Long productId, Pageable pageable) {
        return repository.findByOrder_Product_Id(productId, pageable).map(LogResponseDTO::fromEntity);
    }

    // --- FEATURE: DASHBOARD/KPI ---

    public IOrderStats findOrderStats(Long orderId) {
        return repository.getStatsByOrder(orderId);
    }

    public ProductStatsDTO findProductStats(Long productId) {
        Long safeId = Objects.requireNonNull(productId);

        ProductStatsDTO stats = repository.getStatsByProduct(safeId);
        if (stats != null) {
            return stats;
        }

        var product = productRepository.findById(safeId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        return new ProductStatsDTO(product.getCode(), product.getDescription(), 0L, null, null, null);
    }
}