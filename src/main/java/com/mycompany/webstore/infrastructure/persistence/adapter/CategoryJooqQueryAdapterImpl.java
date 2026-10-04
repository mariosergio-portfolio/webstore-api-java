package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.application.port.out.CategoryQueryRepository;
import com.mycompany.webstore.domain.model.Category;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
public class CategoryJooqQueryAdapterImpl implements CategoryQueryRepository {

    private static final Table<?> CATEGORIES = table("categories");
    private static final Field<UUID> ID = field("id", UUID.class);
    private static final Field<String> NAME = field("name", String.class);
    private static final Field<String> SLUG = field("slug", String.class);
    private static final Field<UUID> PARENT_ID = field("parent_id", UUID.class);
    private static final Field<Integer> SORT_ORDER = field("sort_order", Integer.class);

    private final DSLContext dsl;

    public CategoryJooqQueryAdapterImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<Category> findAll() {
        return dsl.select(ID, NAME, SLUG, PARENT_ID, SORT_ORDER)
                .from(CATEGORIES)
                .orderBy(SORT_ORDER, NAME)
                .fetch(r -> new Category(
                        r.get(ID), r.get(NAME), r.get(SLUG), r.get(PARENT_ID), r.get(SORT_ORDER)));
    }
}
