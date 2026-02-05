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

import br.rafaeros.smp.modules.product.controller.dto.CreateProductDTO;
import br.rafaeros.smp.modules.product.controller.dto.ProductResponseDTO;
import br.rafaeros.smp.modules.product.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid CreateProductDTO dto) {
        return ResponseEntity.status(201).body(productService.createProduct(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAll(@PageableDefault(size = 20, page = 0) Pageable pageable,
            @RequestParam(required = false) String code, @RequestParam(required = false) String description) {
        return ResponseEntity.ok(productService.findAll(pageable, code, description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ProductResponseDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(productService.findByCode(code));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,
            @RequestBody @Valid CreateProductDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
