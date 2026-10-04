package com.mycompany.webstore.infrastructure.rest.catalog;

import com.mycompany.webstore.application.port.in.CategoryPort;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Catalog — Categories (v2)")
@RestController
@RequestMapping("/v2/api/categories")
public class CategoryV2Controller {

    private final CategoryPort categoryPort;
    private final CategoryRestMapper mapper;

    public CategoryV2Controller(CategoryPort categoryPort, CategoryRestMapper mapper) {
        this.categoryPort = categoryPort;
        this.mapper = mapper;
    }

    @Operation(summary = "List all categories (queried with jOOQ instead of JPA)")
    @GetMapping
    public List<CategoryResponse> listCategories() {
        return categoryPort.listCategoriesJooq().stream().map(mapper::toResponse).toList();
    }
}
