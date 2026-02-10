package br.rafaeros.smp.modules.order.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.BusinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.core.utils.DateUtils;
import br.rafaeros.smp.modules.client.model.Client;
import br.rafaeros.smp.modules.client.service.ClientService;
import br.rafaeros.smp.modules.order.controller.OrderSearchFilter;
import br.rafaeros.smp.modules.order.controller.dto.CreateOrderDTO;
import br.rafaeros.smp.modules.order.controller.dto.OrderResponseDTO;
import br.rafaeros.smp.modules.order.controller.dto.OrderSummaryDTO;
import br.rafaeros.smp.modules.order.controller.dto.UpdateOrderDTO;
import br.rafaeros.smp.modules.order.model.Order;
import br.rafaeros.smp.modules.order.model.enums.OrderStatus;
import br.rafaeros.smp.modules.order.repository.OrderRepository;
import br.rafaeros.smp.modules.order.scraper.ErpSearchFilter;
import br.rafaeros.smp.modules.order.scraper.OrderScrapeDTO;
import br.rafaeros.smp.modules.product.model.Product;
import br.rafaeros.smp.modules.product.service.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ClientService clientService;
    private final ErpScraperService scrapeService;

    @Transactional
    public List<OrderResponseDTO> syncFromErp(ErpSearchFilter filter, boolean force) {

        if (filter.getCode() != null && !filter.getCode().isBlank()) {
            Optional<Order> exists = orderRepository.findByCode(filter.getCode());
            if (exists.isPresent()) {
                throw new BusinessException("Ja existe uma OP com o codigo: " + filter.getCode());
            }
        }

        List<OrderScrapeDTO> dtos = scrapeService.fetchOrders(filter);
        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma ordem encontrada no CG com esse filtro.");
        }

        return dtos.stream()
                .map(dto -> processSingleOrder(dto, force))
                .map(OrderResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public OrderResponseDTO createOrder(CreateOrderDTO dto) {
        Product product = productService.findByIdInternal(dto.productId());
        Client client = clientService.findByIdInternal(dto.clientId());
        Optional<Order> existingOpt = orderRepository.findByCode(dto.code());

        if (existingOpt.isPresent()) {
            throw new BusinessException("Ja existe uma OP com o codigo: " + dto.code());
        }

        Order order = new Order();
        order.setCreationDate(Instant.now());
        order.setDeliveryDate(DateUtils.parseBRDate(dto.deliveryDate()));
        order.setCode(dto.code());
        order.setClient(client);
        order.setProduct(product);
        order.setTotalQuantity(dto.totalQuantity());
        order.setProducedQuantity(dto.producedQuantity());
        order.setStatus(mapStatus(dto.status()));

        return OrderResponseDTO.fromEntity(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> findAll(Pageable pageable, OrderSearchFilter filter) {
        Pageable safePage = Objects.requireNonNull(pageable);

        // --- CORREÇÃO DO TYPE SAFETY ---
        // 1. Extraímos para uma variável explícita List<Sort.Order>
        // 2. Usamos .collect(Collectors.toList()) que é mais amigável com inferência de
        // tipos que .toList()
        List<Sort.Order> sortOrders = safePage.getSort().stream()
                .map(order -> {
                    if (order.getProperty().equals("clientName")) {
                        return new Sort.Order(order.getDirection(), "client.name");
                    }
                    if (order.getProperty().equals("productCode")) {
                        return new Sort.Order(order.getDirection(), "product.code");
                    }
                    return order;
                })
                .collect(java.util.stream.Collectors.toList());

        Sort newSort = Sort.by(sortOrders);

        Pageable sortedPageable = PageRequest.of(
                safePage.getPageNumber(),
                safePage.getPageSize(),
                newSort);

        String codeFilter = (filter.getCode() != null && !filter.getCode().isBlank())
                ? "%" + filter.getCode() + "%"
                : null;

        String productCodeFilter = (filter.getProductCode() != null && !filter.getProductCode().isBlank())
                ? "%" + filter.getProductCode() + "%"
                : null;

        Long clientIdFilter = null;
        if (filter.getClientId() != null && !filter.getClientId().isBlank()) {
            try {
                clientIdFilter = Long.parseLong(filter.getClientId());
            } catch (NumberFormatException e) {
            }
        }

        OrderStatus statusFilter = null;
        if (filter.getStatus() != null && !filter.getStatus().isBlank()
                && !filter.getStatus().equalsIgnoreCase("Todos")) {
            try {
                statusFilter = OrderStatus.valueOf(filter.getStatus());
            } catch (IllegalArgumentException e) {
            }
        }

        Instant startDate = Instant.parse("1970-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2100-01-01T23:59:59Z");

        try {
            if (filter.getStartDeliveryDate() != null && !filter.getStartDeliveryDate().isBlank()) {
                startDate = DateUtils.parseBRDate(filter.getStartDeliveryDate());
            }
            if (filter.getEndDeliveryDate() != null && !filter.getEndDeliveryDate().isBlank()) {
                endDate = DateUtils.parseBRDate(filter.getEndDeliveryDate());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de data inválido. Use dd/MM/yyyy");
        }

        return orderRepository.findAllWithFilter(
                sortedPageable,
                codeFilter,
                productCodeFilter,
                clientIdFilter,
                statusFilter,
                startDate,
                endDate).map(OrderResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryDTO> getSummary(Pageable pageable, String code) {
        String searchTerm = (code != null && !code.isBlank())
                ? "%" + code + "%"
                : null;
        return orderRepository.findSummary(pageable, searchTerm);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {
        return OrderResponseDTO.fromEntity(findByIdInternal(id));
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findByCode(String code) {
        return OrderResponseDTO.fromEntity(findByCodeInternal(code));
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, UpdateOrderDTO dto) {
        Order existing = findByIdInternal(id);

        if (existing == null) {
            throw new ResourceNotFoundException("Ordem não encontrada com o ID: " + id);
        }

        if (dto.deliveryDate() != null) {
            existing.setDeliveryDate(DateUtils.parseBRDate(dto.deliveryDate()));
        }
        if (dto.totalQuantity() != null) {
            existing.setTotalQuantity(dto.totalQuantity());
        }
        if (dto.producedQuantity() != null) {
            existing.setProducedQuantity(dto.producedQuantity());
        }

        return OrderResponseDTO.fromEntity(orderRepository.save(existing));
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = findByIdInternal(id);

        if (order == null) {
            throw new ResourceNotFoundException("Ordem não encontrada com o ID: " + id);
        }

        orderRepository.deleteById(Objects.requireNonNull(id));
    }

    @Transactional(readOnly = true)
    public Order findByIdInternal(Long id) {
        Long safeId = Objects.requireNonNull(id);
        return orderRepository.findById(safeId)
        .orElseThrow(() -> new ResourceNotFoundException("Ordem não encontrada com o ID: " + id));
    }
    
    // Private Methods
    @Transactional(readOnly = true)
    private Order findByCodeInternal(String code) {
        String safeCode = Objects.requireNonNull(code);
        return orderRepository.findByCode(safeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem não encontrada com o código: " + code));
    }

    @Transactional
    private Order processSingleOrder(OrderScrapeDTO dto, boolean force) {
        Optional<Order> existingOpt = orderRepository.findByCode(dto.code());
        if (existingOpt.isPresent()) {
            Order existing = existingOpt.get();
            if (!force) {
                return existing;
            }
            return updateOrderMetaData(existing, dto);
        }

        return createNewOrder(dto);
    }

    @Transactional
    private Order updateOrderMetaData(Order existing, OrderScrapeDTO dto) {
        existing.setTotalQuantity(dto.totalQuantity());
        existing.setDeliveryDate(dto.deliveryDate());
        existing.setStatus(mapStatus(dto.status()));
        return orderRepository.save(existing);
    }

    @Transactional
    private Order createNewOrder(OrderScrapeDTO dto) {
        Product product = productService.findByCodeOrCreate(dto.productCode(), dto.productDescription());
        Client client = clientService.findByNameOrCreate(dto.clientName());

        Order order = new Order();
        order.setCreationDate(dto.creationDate());
        order.setDeliveryDate(dto.deliveryDate());
        order.setCode(dto.code());
        order.setClient(client);
        order.setProduct(product);
        order.setTotalQuantity(dto.totalQuantity());
        order.setProducedQuantity(dto.producedQuantity());
        order.setStatus(mapStatus(dto.status()));
        return orderRepository.save(order);
    }

    private OrderStatus mapStatus(String status) {
        return switch (status.toUpperCase()) {
            case "LIBERADA" -> OrderStatus.RELEASED;
            case "INICIADA" -> OrderStatus.STARTED;
            case "FINALIZADA" -> OrderStatus.FINISHED;
            default -> OrderStatus.RELEASED;
        };
    }

}
