package br.rafaeros.smp.modules.order.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.core.exception.BussinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.core.utils.DateUtils;
import br.rafaeros.smp.modules.client.model.Client;
import br.rafaeros.smp.modules.client.service.ClientService;
import br.rafaeros.smp.modules.order.controller.dto.CreateOrderDTO;
import br.rafaeros.smp.modules.order.controller.dto.OrderResponseDTO;
import br.rafaeros.smp.modules.order.controller.dto.UpdateOrderDTO;
import br.rafaeros.smp.modules.order.model.Order;
import br.rafaeros.smp.modules.order.model.enums.OrderStatus;
import br.rafaeros.smp.modules.order.repository.OrderRepository;
import br.rafaeros.smp.modules.order.scraper.ErpSearchFilter;
import br.rafaeros.smp.modules.order.scraper.OrderScrapeDTO;
import br.rafaeros.smp.modules.product.model.Product;
import br.rafaeros.smp.modules.product.service.ProductService;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ClientService clientService;
    private final ErpScraperService scrapeService;

    public OrderService(OrderRepository orderRepository, ProductService productService, ClientService clientService,
            ErpScraperService scrapeService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.clientService = clientService;
        this.scrapeService = scrapeService;
    }

    public List<OrderResponseDTO> syncFromErp(ErpSearchFilter filter, boolean force) {
        List<OrderScrapeDTO> dtos = scrapeService.fetchOrders(filter);
        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma ordem encontrada no CG com esse filtro.");
        }

        return dtos.stream()
                .map(dto -> processSingleOrder(dto, force))
                .map(OrderResponseDTO::fromEntity)
                .toList();
    }

    public OrderResponseDTO createOrder(CreateOrderDTO dto) {
        Product product = productService.findByIdInternal(dto.productId());
        Client client = clientService.findByIdInternal(dto.clientId());
        Optional<Order> existingOpt = orderRepository.findByCode(dto.code());

        if (existingOpt.isPresent()) {
            throw new BussinessException("Ja existe uma OP com o codigo: " + dto.code());
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

    public Page<OrderResponseDTO> findAll(Pageable pageable) {
        Pageable safePage = Objects.requireNonNull(pageable);
        return orderRepository.findAll(safePage).map(OrderResponseDTO::fromEntity);
    }

    public OrderResponseDTO findById(Long id) {
        return OrderResponseDTO.fromEntity(findByIdInternal(id));
    }

    public OrderResponseDTO findByCode(String code) {
        return OrderResponseDTO.fromEntity(findByCodeInternal(code));
    }

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

    public void deleteOrder(Long id) {
        Order order = findByIdInternal(id);

        if (order == null) {
            throw new ResourceNotFoundException("Ordem não encontrada com o ID: " + id);
        }

        orderRepository.deleteById(Objects.requireNonNull(id));
    }

    // Private Methods
    private Order findByIdInternal(Long id) {
        Long safeId = Objects.requireNonNull(id);
        return orderRepository.findById(safeId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem não encontrada com o ID: " + id));
    }

    private Order findByCodeInternal(String code) {
        String safeCode = Objects.requireNonNull(code);
        return orderRepository.findByCode(safeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem não encontrada com o código: " + code));
    }

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

    private Order updateOrderMetaData(Order existing, OrderScrapeDTO dto) {
        existing.setTotalQuantity(dto.totalQuantity());
        existing.setDeliveryDate(dto.deliveryDate());
        return orderRepository.save(existing);
    }

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
        return switch (status) {
            case "LIBERADA" -> OrderStatus.RELEASED;
            case "INICIADA" -> OrderStatus.STARTED;
            case "FINALIZADA" -> OrderStatus.FINISHED;
            default -> OrderStatus.RELEASED;
        };
    }

}
