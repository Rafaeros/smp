package br.rafaeros.smp.modules.order.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.modules.order.controller.dto.CreateOrderDTO;
import br.rafaeros.smp.modules.order.controller.dto.OrderResponseDTO;
import br.rafaeros.smp.modules.order.controller.dto.OrderSummaryDTO;
import br.rafaeros.smp.modules.order.controller.dto.UpdateOrderDTO;
import br.rafaeros.smp.modules.order.scraper.ErpSearchFilter;
import br.rafaeros.smp.modules.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/sync")
    public ResponseEntity<List<OrderResponseDTO>> syncOrders(ErpSearchFilter filter,
            @RequestParam(defaultValue = "false") boolean force) {
        return ResponseEntity.ok(orderService.syncFromErp(filter, force));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody CreateOrderDTO dto) {
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAll(
            @PageableDefault(page = 0, size = 20) Pageable pageable, @ModelAttribute OrderSearchFilter filter) {
        return ResponseEntity.ok(orderService.findAll(pageable, filter));
    }

    @GetMapping("/summary")
    public ResponseEntity<Page<OrderSummaryDTO>> getSummary(@PageableDefault(page = 0, size = 20) Pageable pageable, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(orderService.getSummary(pageable, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<OrderResponseDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(orderService.findByCode(code));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody UpdateOrderDTO dto) {
        return ResponseEntity.ok(orderService.updateOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
