package br.rafaeros.smp.modules.order.controller.dto;

import br.rafaeros.smp.modules.order.model.Order;

public record OrderResponseDTO(
    Long id,
    String code,
    String clientName,
    String productCode,
    String productDescription,
    String creationDate,
    String deliveryDate,
    Integer totalQuantity,
    Integer producedQuantity,
    String status
) {

    public static OrderResponseDTO fromEntity(Order order) {
        return new OrderResponseDTO(
            order.getId(),
            order.getCode(),
            order.getClient() != null ? order.getClient().getName() : null,
            order.getProduct() != null ? order.getProduct().getCode() : null,
            order.getProduct() != null ? order.getProduct().getDescription() : null,
            order.getCreationDate() != null ? order.getCreationDate().toString() : null,
            order.getDeliveryDate() != null ? order.getDeliveryDate().toString() : null,
            order.getTotalQuantity(),
            order.getProducedQuantity(),
            order.getStatus() != null ? order.getStatus().name() : null
        );
    }
}