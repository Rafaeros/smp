package br.rafaeros.smp.modules.order.scraper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class ErpHtmlParser {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<OrderScrapeDTO> parseOrderTable(Document doc) {
        List<OrderScrapeDTO> results = new ArrayList<>();
        Elements rows = doc.select("tbody tr");
        if (rows.isEmpty()) {
            return results;
        }
        for (Element row : rows) {
            try {
                if (row.select("td").size() < 9) {
                    continue;
                }

                OrderScrapeDTO dto = parseRow(row);
                if (dto != null) {
                    results.add(dto);
                }

            } catch (Exception e) {
                throw new RuntimeException("Erro ao processar linha.", e);
            }
        }
        return results;
    }

    private OrderScrapeDTO parseRow(Element row) {
        Elements cols = row.select("td");
        try {
            var creationDate = parseDate(cols.get(0).text().trim());
            var deliveryDate = parseDate(cols.get(1).text().trim());

            String code = cols.get(2).text().trim();
            String clientName = cols.get(3).text().trim();
            String productCode = cols.get(4).text().trim();
            String productDescription = cols.get(5).text().trim();

            Integer totalQuantity = parseInteger(cols.get(6).text().trim());
            Integer producedQuantity = parseInteger(cols.get(7).text().trim());

            String status = cols.get(8).text().trim();

            return new OrderScrapeDTO(code, clientName, productCode, productDescription, totalQuantity,
                    producedQuantity, status, creationDate, deliveryDate);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao parsear dados do pedido.", e);
        }
    }

    private Instant parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            LocalDateTime ldt = LocalDateTime.parse(dateStr, DATE_FMT);
            return ldt.atZone(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(String str) {
        if (str == null || str.isEmpty())
            return 0;
        try {
            String clean = str.replace(".", "").split(",")[0].trim();
            return Integer.parseInt(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
