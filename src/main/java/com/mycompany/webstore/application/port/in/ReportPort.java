package com.mycompany.webstore.application.port.in;

import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.City;
import com.mycompany.webstore.domain.model.CityDistance;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import com.mycompany.webstore.domain.model.Supplier;

import java.util.List;

public interface ReportPort {

    List<Category> listCategories();

    List<ProductWithCategory> listProductsWithCategory();

    List<Supplier> listSuppliers();

    List<City> listCities();

    /**
     * Cities within {@code radiusKm} of the named city (the city itself excluded), nearest first.
     *
     * @param country optional; required only when the city name is ambiguous
     */
    List<CityDistance> listCitiesWithinKm(String cityName, String country, double radiusKm);
}
