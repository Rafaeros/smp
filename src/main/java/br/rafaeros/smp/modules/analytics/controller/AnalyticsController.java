package br.rafaeros.smp.modules.analytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.modules.analytics.controller.dto.DashboardDTO;
import br.rafaeros.smp.modules.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDTO.DashboardSummaryDTO>> getDashboardSummary() {
        DashboardDTO.DashboardSummaryDTO summary = analyticsService.getDashboardSummary();
        
        return ResponseEntity.ok(
            ApiResponse.success("Dashboard carregado com sucesso", summary)
        );
    }
}