package br.rafaeros.smp.modules.order.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.exception.BussinessException;
import br.rafaeros.smp.modules.order.controller.dto.OrderResponseDTO;
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
    public ResponseEntity<List<OrderResponseDTO>> syncOrders(ErpSearchFilter filter, @RequestParam(defaultValue = "false") boolean force) {
        if (filter.getCode().isEmpty() && filter.getStatus().equals("Todos")) {
            throw new BussinessException("Insira algum filtro ou desmaque a opção Todos");
        }

        return ResponseEntity.ok(orderService.syncFromErp(filter, force));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder() {
        return ResponseEntity.ok(orderService.createOrder());
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAll(
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }
}
