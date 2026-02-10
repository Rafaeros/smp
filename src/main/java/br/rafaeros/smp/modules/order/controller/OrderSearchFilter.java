package br.rafaeros.smp.modules.order.controller;

import java.time.Instant;
import java.time.ZoneId;

import br.rafaeros.smp.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSearchFilter {

    private static final ZoneId ZONE_BR = ZoneId.of("America/Sao_Paulo");

    @Builder.Default
    private String code = "";

    @Builder.Default
    private String productCode = "";

    @Builder.Default
    private String clientId = null;

    @Builder.Default
    private String status = "";

    @Builder.Default
    private String startDeliveryDate = defaultStartDeliveryDate();

    @Builder.Default
    private String endDeliveryDate = defaultEndDeliveryDate();

    private static String defaultStartDeliveryDate() {
        Instant now = Instant.now();
        Instant minusOneMonth = now.atZone(ZONE_BR).minusMonths(1).toInstant();
        return DateUtils.toBRDate(minusOneMonth);
    }

    private static String defaultEndDeliveryDate() {
        Instant now = Instant.now();
        Instant plusTwoMonths = now.atZone(ZONE_BR).plusMonths(2).toInstant();
        return DateUtils.toBRDate(plusTwoMonths);
    }

    public static OrderSearchFilter byCode(String code) {
        return OrderSearchFilter.builder().code(code).build();
    }
}