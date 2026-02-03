package br.rafaeros.smp.modules.order.service;

import java.util.List;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.modules.order.scraper.ErpClient;
import br.rafaeros.smp.modules.order.scraper.ErpHtmlParser;
import br.rafaeros.smp.modules.order.scraper.ErpSearchFilter;
import br.rafaeros.smp.modules.order.scraper.OrderScrapeDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ErpScraperService {
    private final ErpClient erpClient;
    private final ErpHtmlParser parser;

    public ErpScraperService(ErpClient erpClient, ErpHtmlParser parser) {
        this.erpClient = erpClient;
        this.parser = parser;
    }

    public List<OrderScrapeDTO> fetchOrders(ErpSearchFilter filter) {
        Document doc = erpClient.searchOrders(filter);
        List<OrderScrapeDTO> orders = parser.parseOrderTable(doc);
        log.info("Scraper encontrou {} ordens para o filtro: {}", orders.size(), filter);
        return orders;
    }
}
