package br.rafaeros.smp.modules.order.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSearchFilter {

    @Builder.Default
    private String code = "";

    @Builder.Default
    private String productCode = "";

    @Builder.Default
    private String clientId = null;

    @Builder.Default
    private String status = "";

    @Builder.Default
    private String startDeliveryDate = "";

    @Builder.Default
    private String endDeliveryDate = "";

    public static OrderSearchFilter byCode(String code) {
        return OrderSearchFilter.builder().code(code).build();
    }
}