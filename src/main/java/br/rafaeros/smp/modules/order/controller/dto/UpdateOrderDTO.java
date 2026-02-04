package br.rafaeros.smp.modules.order.controller.dto;

public record UpdateOrderDTO (
        String deliveryDate,
        Integer totalQuantity,
        Integer producedQuantity
) {}