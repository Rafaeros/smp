package br.rafaeros.smp.modules.product.controller.dto;

import br.rafaeros.smp.modules.product.model.Product;

public record ProductListDTO(
        Long id,
        String code) {
    public static ProductListDTO fromEntity(Product product) {
        return new ProductListDTO(
                product.getId(),
                product.getCode());
    }

}
