package com.mycompany.webstore.application.port.out;

import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.City;
import com.mycompany.webstore.domain.model.CityDistance;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import com.mycompany.webstore.domain.model.Supplier;

import java.util.List;

/** Read-only reporting queries, implemented with jOOQ. */
public interface ReportQueryRepository {

    List<Category> findAllCategories();

    List<ProductWithCategory> findAllProductsWithCategory();

    List<Supplier> findAllSuppliers();

    List<City> findAllCities();

    /** Case-insensitive name match, optionally narrowed by country. */
    List<City> findCitiesByName(String name, String country);

    /** Cities within {@code radiusKm} of {@code origin}, excluding the origin itself, nearest first. */
    List<CityDistance> findCitiesWithinKm(City origin, double radiusKm);
}
