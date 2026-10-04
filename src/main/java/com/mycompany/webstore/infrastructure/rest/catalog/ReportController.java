package com.mycompany.webstore.infrastructure.rest.catalog;

import com.mycompany.webstore.application.port.in.ReportPort;
import com.mycompany.webstore.domain.model.Supplier;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CategoryResponse;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CityResponse;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.SupplierResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Reports (jOOQ)")
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportPort reportPort;
    private final CategoryRestMapper mapper;

    public ReportController(ReportPort reportPort, CategoryRestMapper mapper) {
        this.reportPort = reportPort;
        this.mapper = mapper;
    }

    @Operation(summary = "Categories report (queried with jOOQ)")
    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return reportPort.listCategories().stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Suppliers report with address city and coordinates (queried with jOOQ)")
    @GetMapping("/suppliers")
    public List<SupplierResponse> suppliers() {
        return reportPort.listSuppliers().stream().map(this::toResponse).toList();
    }

    private SupplierResponse toResponse(Supplier s) {
        var c = s.addressCity();
        return new SupplierResponse(s.id(), s.name(), s.email(),
                new CityResponse(c.id(), c.name(), c.state(), c.country(), c.latitude(), c.longitude()),
                s.productCount());
    }
}
