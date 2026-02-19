package br.rafaeros.smp.modules.product.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.BusinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.core.utils.CsvUtils;
import br.rafaeros.smp.modules.product.controller.dto.CreateProductDTO;
import br.rafaeros.smp.modules.product.controller.dto.ProductExportDTO;
import br.rafaeros.smp.modules.product.controller.dto.ProductResponseDTO;
import br.rafaeros.smp.modules.product.model.Product;
import br.rafaeros.smp.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDTO createProduct(CreateProductDTO dto) {
        boolean exits = productRepository.existsByCode(dto.code());
        if (exits) {
            throw new ResourceNotFoundException("Produto já cadastrado com o código: " + dto.code());
        }
        Product product = new Product();
        product.setCode(dto.code());
        product.setDescription(dto.description());
        Product saved = productRepository.save(product);
        return ProductResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Pageable pageable, String code, String description) {
        Pageable safePage = Objects.requireNonNull(pageable);

        String codeFilter = (code != null && !code.isBlank()) ? "%" + code + "%" : null;
        String descFilter = (description != null && !description.isBlank()) ? "%" + description + "%" : null;

        if (codeFilter == null && descFilter == null) {
            return productRepository.findAll(safePage).map(ProductResponseDTO::fromEntity);
        }

        return productRepository.findByFilters(codeFilter, descFilter, safePage)
                .map(ProductResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getSummary(String query, Pageable pageable) {
        Pageable safePage = Objects.requireNonNull(pageable);

        if (query == null || query.isBlank()) {
            return productRepository.findAll(safePage).map(ProductResponseDTO::fromEntity);
        }

        String searchTerm = "%" + query + "%";

        return productRepository.searchByTerm(searchTerm, safePage)
                .map(ProductResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        Product product = findByIdInternal(id);
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findByCode(String code) {
        Product product = findByCodeInternal(code);
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, CreateProductDTO dto) {
        Product product = findByIdInternal(id);
        product.setCode(dto.code());
        product.setDescription(dto.description());
        productRepository.save(product);
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Long safeId = Objects.requireNonNull(id);
        if (!productRepository.existsById(safeId)) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }

        try {
            productRepository.deleteById(safeId);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(
                    "Não é possível excluir este produto pois ele já possui Ordens de Produção vinculadas.");
        }
    }

    @Transactional
    public Product findByCodeOrCreate(String code, String description) {
        try {
            return findByCodeInternal(code);
        } catch (ResourceNotFoundException ex) {
            Product product = new Product();
            product.setCode(code);
            product.setDescription(description);
            return productRepository.save(product);
        }
    }

    @Transactional(readOnly = true)
    public Product findByIdInternal(Long id) {
        Long safeId = Objects.requireNonNull(id);
        return productRepository.findById(safeId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado pelo ID: " + id));
    }

    @Transactional(readOnly = true)
    private Product findByCodeInternal(String code) {
        String safeCode = Objects.requireNonNull(code);
        return productRepository.findByCode(safeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado pelo código: " + code));
    }

    @Transactional(readOnly = true)
    public byte[] exportProductToCsv() {
        List<ProductExportDTO> stats = productRepository.getProductExportStats();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("Código Produto;Descrição;Total de Registros;Tempo Mínimo (s);Tempo Médio (s);Tempo Máximo (s)");

        for (ProductExportDTO p : stats) {
            pw.printf("%s;%s;%d;%s;%s;%s%n",
                    CsvUtils.escapeCsv(p.productCode()),
                    CsvUtils.escapeCsv(p.productDescription()),
                    p.totalLogs(),
                    CsvUtils.formatDouble(p.minTime()),
                    CsvUtils.formatDouble(p.avgTime()),
                    CsvUtils.formatDouble(p.maxTime()));
        }

        return CsvUtils.generateCsvBytes(sw);
    }

}
