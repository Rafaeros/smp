package br.rafaeros.smp.modules.order.controller.dto;

import br.rafaeros.smp.modules.order.model.Order;

public record OrderSummaryDTO(
        Long id,
        String code
    ) {
    public static OrderSummaryDTO fromEntity(Order order) {
        return new OrderSummaryDTO(order.getId(), order.getCode());
    }
}
