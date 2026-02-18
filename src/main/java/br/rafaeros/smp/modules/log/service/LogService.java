package br.rafaeros.smp.modules.log.service;

import java.time.Instant;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.repository.DeviceRepository;
import br.rafaeros.smp.modules.device.service.DeviceService;
import br.rafaeros.smp.modules.log.controller.dto.CreateLogRequestDTO;
import br.rafaeros.smp.modules.log.controller.dto.DeviceLogPayloadDTO;
import br.rafaeros.smp.modules.log.controller.dto.IOrderStats;
import br.rafaeros.smp.modules.log.controller.dto.LogResponseDTO;
import br.rafaeros.smp.modules.log.controller.dto.ProductStatsDTO;
import br.rafaeros.smp.modules.log.model.Log;
import br.rafaeros.smp.modules.log.repository.LogRepository;
import br.rafaeros.smp.modules.order.model.Order;
import br.rafaeros.smp.modules.order.repository.OrderRepository;
import br.rafaeros.smp.modules.order.service.OrderService;
import br.rafaeros.smp.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

    private final OrderService orderService;
    private final DeviceService deviceService;

    private final OrderRepository orderRepository;
    private final DeviceRepository deviceRepository;
    private final LogRepository logRepository;
    private final ProductRepository productRepository;

    @Transactional
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

        Log saved = logRepository.save(log);
        return LogResponseDTO.fromEntity(saved);
    }   

    @Transactional
    public void registerLogFromDevice(String macAddress, DeviceLogPayloadDTO payload) {
        Device device = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo não encontrado: " + macAddress));

        Order order = orderRepository.findById(Objects.requireNonNull(payload.orderId()))
                .orElseThrow(() -> new ResourceNotFoundException("Ordem não encontrada: " + payload.orderId()));


        Log log = new Log();
        log.setDevice(device);
        log.setOrder(order);
        log.setCreatedAt(Instant.now());
        log.setCycleTime(payload.cycleTime());
        log.setPausedTime(payload.pausedTime());
        log.setQuantityProduced(payload.quantityProduced());
        log.setQuantityPaused(payload.quantityPaused());

        logRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<LogResponseDTO> findAll(Pageable pageable) {
        Pageable safePage = Objects.requireNonNull(pageable);
        return logRepository.findAll(safePage).map(LogResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LogResponseDTO> findLogsByOrder(Pageable pageable, Long orderId) {
        return logRepository.findByOrderId(orderId, pageable).map(LogResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LogResponseDTO> findLogsByProduct(Long productId, Pageable pageable) {
        return logRepository.findByOrder_Product_Id(productId, pageable).map(LogResponseDTO::fromEntity);
    }

    // --- FEATURE: DASHBOARD/KPI ---

    @Transactional(readOnly = true)
    public IOrderStats findOrderStats(Long orderId) {
        return logRepository.getStatsByOrder(orderId);
    }

    @Transactional(readOnly = true)
    public ProductStatsDTO findProductStats(Long productId) {
        Long safeId = Objects.requireNonNull(productId);

        ProductStatsDTO stats = logRepository.getStatsByProduct(safeId);
        if (stats != null) {
            return stats;
        }

        var product = productRepository.findById(safeId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        return new ProductStatsDTO(product.getCode(), product.getDescription(), 0L, null, null, null);
    }
}