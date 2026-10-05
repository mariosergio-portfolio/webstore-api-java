package com.mycompany.webstore.infrastructure.rest.report;

import com.mycompany.webstore.application.port.in.ReportPort;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import com.mycompany.webstore.domain.model.City;
import com.mycompany.webstore.domain.model.Supplier;
import com.mycompany.webstore.infrastructure.rest.catalog.CategoryRestMapper;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CategoryResponse;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CityResponse;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.ProductWithCategoryResponse;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.SupplierResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CityDistanceResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Reports (jOOQ)")
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportPort reportPort;
    private final CategoryRestMapper categoryMapper;

    private final CategoryRestMapper mapper;


    public ReportController(ReportPort reportPort, CategoryRestMapper categoryMapper, CategoryRestMapper mapper) {
        this.reportPort = reportPort;
        this.categoryMapper = categoryMapper;
        this.mapper = mapper;
    }

    @Operation(summary = "Categories report (queried with jOOQ)")
    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return reportPort.listCategories().stream().map(mapper::toResponse).toList();
    }


    @Operation(summary = "List products with their category info (queried with jOOQ)")
    @GetMapping("/products")
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


    @Operation(summary = "Cities report with coordinates (queried with jOOQ)")
    @GetMapping("/cities")
    public List<CityResponse> cities() {
        return reportPort.listCities().stream().map(this::toResponse).toList();
    }

    @Operation(summary = "Cities within radiusKm of a city, nearest first (PostGIS on PostgreSQL)")
    @GetMapping("/cities/nearby")
    public List<CityDistanceResponse> citiesNearby(
            @RequestParam String city,
            @RequestParam(required = false) String country,
            @RequestParam double radiusKm) {
        return reportPort.listCitiesWithinKm(city, country, radiusKm).stream()
                .map(d -> new CityDistanceResponse(d.city().id(), d.city().name(), d.city().state(),
                        d.city().country(), d.city().latitude(), d.city().longitude(),
                        Math.round(d.distanceKm() * 10.0) / 10.0))
                .toList();
    }

    @Operation(summary = "Suppliers report with address city and coordinates (queried with jOOQ)")
    @GetMapping("/suppliers")
    public List<SupplierResponse> suppliers() {
        return reportPort.listSuppliers().stream().map(this::toResponse).toList();
    }

    private SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(s.id(), s.name(), s.email(), toResponse(s.addressCity()), s.productCount());
    }

    private CityResponse toResponse(City c) {
        return new CityResponse(c.id(), c.name(), c.state(), c.country(), c.latitude(), c.longitude());
    }
}
