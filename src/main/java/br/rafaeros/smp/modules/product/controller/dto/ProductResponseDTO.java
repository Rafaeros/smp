package br.rafaeros.smp.modules.product.controller.dto;

import br.rafaeros.smp.modules.product.model.Product;

public record ProductResponseDTO(
        Long id,
        String code,
        String description) {
    public static ProductResponseDTO fromEntity(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getCode(),
                product.getDescription());
    }
}
