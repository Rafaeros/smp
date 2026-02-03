package br.rafaeros.smp.modules.order.scraper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpSearchFilter {

    @Builder.Default
    private String code = "";

    @Builder.Default
    private String clientName = "";

    @Builder.Default
    private String productCode = "";

    @Builder.Default
    private String status = "Todos";

    @Builder.Default
    private String startDeliveryDate = "";

    @Builder.Default
    private String endDeliveryDate = "";

    public static ErpSearchFilter byCode(String code) {
        return ErpSearchFilter.builder().code(code).build();
    }
}