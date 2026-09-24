package com.mycompany.webstore.infrastructure.rest.catalog;

import com.mycompany.webstore.application.port.in.CategoryPort;
import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CategoryRequest;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Catalog — Categories")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryPort categoryPort;
    private final CategoryRestMapper mapper;

    public CategoryController(CategoryPort categoryPort, CategoryRestMapper mapper) {
        this.categoryPort = categoryPort;
        this.mapper = mapper;
    }

    @Operation(summary = "List all categories")
    @GetMapping
    public List<CategoryResponse> listCategories() {
        return categoryPort.listCategories().stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public CategoryResponse getCategory(@PathVariable UUID id) {
        return mapper.toResponse(categoryPort.getCategoryById(id));
    }

    @Operation(summary = "Create a new category (admin)")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category created = categoryPort.createCategory(mapper.toDomain(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }

    @Operation(summary = "Update a category (admin)")
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable UUID id,
                                           @Valid @RequestBody CategoryRequest request) {
        return mapper.toResponse(categoryPort.updateCategory(id, mapper.toDomain(request)));
    }

    @Operation(summary = "Delete a category (admin)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryPort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
