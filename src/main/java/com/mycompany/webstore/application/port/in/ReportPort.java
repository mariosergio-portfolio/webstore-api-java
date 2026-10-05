package com.mycompany.webstore.application.port.in;

import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.City;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import com.mycompany.webstore.domain.model.Supplier;

import java.util.List;

public interface ReportPort {

    List<Category> listCategories();

    List<ProductWithCategory> listProductsWithCategory();

    List<Supplier> listSuppliers();

    List<City> listCities();
}
