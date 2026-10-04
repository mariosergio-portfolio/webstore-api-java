package com.mycompany.webstore.application.port.out;

import com.mycompany.webstore.domain.model.Category;

import java.util.List;

/** Read-only query port, implemented with jOOQ. */
public interface CategoryQueryRepository {

    List<Category> findAll();
}
