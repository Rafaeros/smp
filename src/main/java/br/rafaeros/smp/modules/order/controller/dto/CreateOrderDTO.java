package br.rafaeros.smp.modules.order.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderDTO (

    @NotBlank(message = "O número da OP deve ser informado")
    String code,

    @NotBlank(message = "O cliente deve ser informado")
    Long clientId,

    @NotBlank(message = "O produto deve ser informado")
    Long productId,

    @NotBlank(message = "A data de entrega deve ser informada")
    String deliveryDate,

    @NotBlank(message = "A quantidade total deve ser informada")
    Integer totalQuantity,
    
    @NotBlank(message = "A quantidade produzida deve ser informada")
    Integer producedQuantity,

    @NotBlank(message = "O status deve ser LIBERADA, INICIADA ou FINALIZADA")
    String status
) {}
