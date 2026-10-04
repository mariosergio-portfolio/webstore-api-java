package com.mycompany.webstore.application.port.out;

import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import com.mycompany.webstore.domain.model.Supplier;

import java.util.List;

/** Read-only reporting queries, implemented with jOOQ. */
public interface ReportQueryRepository {

    List<Category> findAllCategories();

    List<ProductWithCategory> findAllProductsWithCategory();

    List<Supplier> findAllSuppliers();
}
