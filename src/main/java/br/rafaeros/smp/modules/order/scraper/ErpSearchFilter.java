package br.rafaeros.smp.modules.order.scraper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpSearchFilter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    public String getStartDeliveryDate() {
        if (this.startDeliveryDate == null || this.startDeliveryDate.trim().isEmpty()) {
            return LocalDate.now().minusMonths(1).format(DATE_FMT);
        }
        return this.startDeliveryDate;
    }

    public String getEndDeliveryDate() {
        if (this.endDeliveryDate == null || this.endDeliveryDate.trim().isEmpty()) {
            return LocalDate.now().plusMonths(2).format(DATE_FMT);
        }
        return this.endDeliveryDate;
    }

    public static ErpSearchFilter byCode(String code) {
        return ErpSearchFilter.builder()
                .code(code)
                .build();
    }
}