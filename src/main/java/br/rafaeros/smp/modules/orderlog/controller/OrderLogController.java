package br.rafaeros.smp.modules.orderlog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.modules.orderlog.controller.dto.OrderLogResponseDTO;
import br.rafaeros.smp.modules.orderlog.controller.dto.OrderStatsDTO;
import br.rafaeros.smp.modules.orderlog.controller.dto.ProductStatsDTO;
import br.rafaeros.smp.modules.orderlog.service.OrderLogService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order-logs")
@RequiredArgsConstructor
public class OrderLogController {

    private final OrderLogService orderLogService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Page<OrderLogResponseDTO>>> getLogsByOrder(
            @PageableDefault(page = 0, size = 20) Pageable pageable, @PathVariable Long orderId) {
        return ResponseEntity.ok(
                ApiResponse.success("Logs listados com sucesso.", orderLogService.findLogsByOrder(pageable, orderId)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<OrderLogResponseDTO>>> getLogsByProduct(
            @PageableDefault(page = 0, size = 20) Pageable pageable, @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Logs listados com sucesso.",
                orderLogService.findLogsByProduct(productId, pageable)));
    }

    @GetMapping("/stats/order/{orderId}")
    public ResponseEntity<ApiResponse<OrderStatsDTO>> getOrderStats(@PathVariable Long orderId) {
        return ResponseEntity
                .ok(ApiResponse.success("Logs listados com sucesso.", orderLogService.findOrderStats(orderId)));
    }

    @GetMapping("/stats/product/{productId}")
    public ResponseEntity<ApiResponse<ProductStatsDTO>> getProductStats(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Logs listados com sucesso.",
                orderLogService.findProductStats(productId)));
    }
}
