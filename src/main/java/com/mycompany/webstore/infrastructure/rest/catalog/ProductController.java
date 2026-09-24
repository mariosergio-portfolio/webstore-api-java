package com.mycompany.webstore.infrastructure.rest.catalog;

import com.mycompany.webstore.application.port.in.ProductPort;
import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.ProductRequest;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@Tag(name = "Catalog — Products")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductPort productPort;
    private final ProductRestMapper mapper;

    public ProductController(ProductPort productPort, ProductRestMapper mapper) {
        this.productPort = productPort;
        this.mapper = mapper;
    }

    @Operation(summary = "List or search products")
    @GetMapping
    public Page<ProductResponse> listProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ProductStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        return productPort.listProducts(q, categoryId, minPrice, maxPrice, status, pageable)
                .map(mapper::toResponse);
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable UUID id) {
        return mapper.toResponse(productPort.getProductById(id));
    }

    @Operation(summary = "Create a new product (admin)")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product created = productPort.createProduct(mapper.toDomain(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }

    @Operation(summary = "Full update of a product (admin)")
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable UUID id,
                                         @Valid @RequestBody ProductRequest request) {
        return mapper.toResponse(productPort.updateProduct(id, mapper.toDomain(request)));
    }

    @Operation(summary = "Partial update of a product (admin)")
    @PatchMapping("/{id}")
    public ProductResponse patchProduct(@PathVariable UUID id,
                                        @RequestBody ProductRequest request) {
        return mapper.toResponse(productPort.patchProduct(id, mapper.toDomain(request)));
    }

    @Operation(summary = "Archive a product (admin soft-delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiveProduct(@PathVariable UUID id) {
        productPort.archiveProduct(id);
        return ResponseEntity.noContent().build();
    }
}
