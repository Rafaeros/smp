package br.rafaeros.smp.modules.analytics.service;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.modules.analytics.controller.dto.DashboardDTO;
import br.rafaeros.smp.modules.log.repository.LogRepository;
import br.rafaeros.smp.modules.log.controller.dto.IOrderStats;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final LogRepository logRepository;

    public DashboardDTO.DashboardSummaryDTO getDashboardSummary() {
        Instant todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant now = Instant.now();
        Instant yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant yesterdayEnd = todayStart.minusMillis(1);

        IOrderStats statsToday = logRepository.getGlobalStatsByRange(todayStart, now);
        IOrderStats statsYesterday = logRepository.getGlobalStatsByRange(yesterdayStart, yesterdayEnd);

        List<DashboardDTO.DashboardKPIDTO> kpis = new ArrayList<>();

        long prodToday = statsToday.getQuantityProduced();
        long prodYesterday = statsYesterday.getQuantityProduced();
        kpis.add(new DashboardDTO.DashboardKPIDTO(
            "Produção Hoje",
            String.valueOf(prodToday),
            calculateTrend(prodToday, prodYesterday, true),
            getTrendDirection(prodToday, prodYesterday, true),
            "production"
        ));

        double cycleToday = statsToday.getAvgCycleTime();
        double cycleYesterday = statsYesterday.getAvgCycleTime();
        kpis.add(new DashboardDTO.DashboardKPIDTO(
            "Ciclo Médio",
            String.valueOf((int) cycleToday),
            calculateTrend(cycleToday, cycleYesterday, false),
            getTrendDirection(cycleToday, cycleYesterday, false),
            "energy"
        ));

        double efficiency = (cycleToday > 0) ? Math.min(100.0, (60.0 / cycleToday) * 100) : 0.0;
        kpis.add(new DashboardDTO.DashboardKPIDTO(
            "Eficiência Global",
            String.format("%.1f%%", efficiency),
            "OEE Estimado",
            "neutral",
            "production"
        ));

        long alerts = statsToday.getTotalLogs() > 0 ? 0 : 1; 
        kpis.add(new DashboardDTO.DashboardKPIDTO(
            "Alertas Ativos",
            String.valueOf(alerts),
            alerts > 0 ? "Requer atenção" : "Sistema estável",
            alerts > 0 ? "down" : "neutral",
            "alerts"
        ));

        List<DashboardDTO.DashboardLogDTO> recentLogs = logRepository.findRecentLogs(PageRequest.of(0, 10)).stream()
            .map(log -> new DashboardDTO.DashboardLogDTO(
                log.getId(),
                log.getDevice().getMacAddress(),
                "OP " + log.getOrder().getCode() + ": " + log.getQuantityProduced() + " un.",
                formatSmartDate(log.getCreatedAt()),
                log.getQuantityProduced() > 0 ? "info" : "warning"
            )).toList();

        return new DashboardDTO.DashboardSummaryDTO(kpis, recentLogs);
    }

    private String calculateTrend(double current, double previous, boolean higherIsBetter) {
        if (previous <= 0) return "Sem dados anteriores";
        double diff = ((current - previous) / previous) * 100;
        return String.format("%s%.1f%% vs ontem", (diff > 0 ? "+" : ""), diff);
    }

    private String getTrendDirection(double current, double previous, boolean higherIsBetter) {
        if (Math.abs(current - previous) < 0.1) return "neutral";
        if (higherIsBetter) {
            return current > previous ? "up" : "down";
        } else {
            return current < previous ? "up" : "down";
        }
    }

    private String formatSmartDate(Instant timestamp) {
        if (timestamp == null) return "--";
        long seconds = Duration.between(timestamp, Instant.now()).getSeconds();
        if (seconds < 60) return "agora";
        if (seconds < 3600) return (seconds / 60) + " min atrás";
        if (seconds < 86400) return (seconds / 3600) + " h atrás";
        LocalDateTime dt = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        return dt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }
}