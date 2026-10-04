package com.mycompany.webstore.infrastructure.rest.catalog;

import com.mycompany.webstore.application.port.in.ReportPort;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.ProductWithCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Catalog — Products (v2, jOOQ)")
@RestController
@RequestMapping("/v2/api/product")
public class ProductV2Controller {

    private final ReportPort reportPort;
    private final CategoryRestMapper categoryMapper;

    public ProductV2Controller(ReportPort reportPort, CategoryRestMapper categoryMapper) {
        this.reportPort = reportPort;
        this.categoryMapper = categoryMapper;
    }

    @Operation(summary = "List products with their category info (queried with jOOQ)")
    @GetMapping
    public List<ProductWithCategoryResponse> listProducts() {
        return reportPort.listProductsWithCategory().stream().map(this::toResponse).toList();
    }

    private ProductWithCategoryResponse toResponse(ProductWithCategory pc) {
        var p = pc.product();
        return new ProductWithCategoryResponse(
                p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getPrice(),
                p.getCurrency(), p.getStockQuantity(), p.getImageUrls(), p.getStatus(),
                p.getCreatedAt(), p.getUpdatedAt(),
                pc.category() == null ? null : categoryMapper.toResponse(pc.category()));
    }
}
