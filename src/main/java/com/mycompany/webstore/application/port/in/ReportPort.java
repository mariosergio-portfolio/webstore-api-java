package com.mycompany.webstore.application.port.in;

import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.ProductWithCategory;

import java.util.List;

public interface ReportPort {

    List<Category> listCategories();

    List<ProductWithCategory> listProductsWithCategory();
}
