package br.rafaeros.smp.modules.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.modules.product.controller.dto.CreateProductDTO;
import br.rafaeros.smp.modules.product.controller.dto.ProductResponseDTO;
import br.rafaeros.smp.modules.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@RequestBody @Valid CreateProductDTO dto) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Produto criado com sucesso!", productService.createProduct(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getAll(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            @RequestParam(required = false) String code, @RequestParam(required = false) String description) {
        return ResponseEntity.ok(ApiResponse.success("Produtos listados com sucesso.",
                productService.findAll(pageable, code, description)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getSummary(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success("Produtos listados.",
                productService.getSummary(query, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Produto encontrado.", productService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable Long id,
            @RequestBody @Valid CreateProductDTO dto) {
        return ResponseEntity
                .ok(ApiResponse.success("Produto atualizado com sucesso!", productService.updateProduct(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Produto removido com sucesso!"));
    }

}
