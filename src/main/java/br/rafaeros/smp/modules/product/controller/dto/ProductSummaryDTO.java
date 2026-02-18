package br.rafaeros.smp.modules.product.controller.dto;

import br.rafaeros.smp.modules.product.model.Product;

public record ProductSummaryDTO (
    Long id,
    String code
) {
    public static ProductSummaryDTO fromEntity(Product product) {
        return new ProductSummaryDTO(product.getId(), product.getCode());
    }
}
