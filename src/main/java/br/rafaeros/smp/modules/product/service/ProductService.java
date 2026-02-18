package br.rafaeros.smp.modules.product.service;

import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.core.exception.BusinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.product.controller.dto.CreateProductDTO;
import br.rafaeros.smp.modules.product.controller.dto.ProductResponseDTO;
import br.rafaeros.smp.modules.product.controller.dto.ProductSummaryDTO;
import br.rafaeros.smp.modules.product.model.Product;
import br.rafaeros.smp.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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

    public Page<ProductSummaryDTO> getSummary(String query, Pageable pageable) {
        Pageable safePage = Objects.requireNonNull(pageable);

        if (query == null || query.isBlank()) {
            return productRepository.findAll(safePage).map(ProductSummaryDTO::fromEntity);
        }

        String searchTerm = "%" + query + "%";

        return productRepository.searchByTerm(searchTerm, safePage)
                .map(ProductSummaryDTO::fromEntity);
    }

    public ProductResponseDTO findById(Long id) {
        Product product = findByIdInternal(id);
        return ProductResponseDTO.fromEntity(product);
    }

    public ProductResponseDTO findByCode(String code) {
        Product product = findByCodeInternal(code);
        return ProductResponseDTO.fromEntity(product);
    }

    public ProductResponseDTO updateProduct(Long id, CreateProductDTO dto) {
        Product product = findByIdInternal(id);
        product.setCode(dto.code());
        product.setDescription(dto.description());
        productRepository.save(product);
        return ProductResponseDTO.fromEntity(product);
    }

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

    public Product findByIdInternal(Long id) {
        Long safeId = Objects.requireNonNull(id);
        return productRepository.findById(safeId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado pelo ID: " + id));
    }

    private Product findByCodeInternal(String code) {
        String safeCode = Objects.requireNonNull(code);
        return productRepository.findByCode(safeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado pelo código: " + code));
    }

}
