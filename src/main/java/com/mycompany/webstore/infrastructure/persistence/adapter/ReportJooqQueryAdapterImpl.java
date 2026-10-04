package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.webstore.application.port.out.ReportQueryRepository;
import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.ImageUrl;
import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
public class ReportJooqQueryAdapterImpl implements ReportQueryRepository {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Table<?> CATEGORIES = table("categories").as("c");
    private static final Field<UUID> C_ID = field("c.id", UUID.class);
    private static final Field<String> C_NAME = field("c.name", String.class);
    private static final Field<String> C_SLUG = field("c.slug", String.class);
    private static final Field<UUID> C_PARENT_ID = field("c.parent_id", UUID.class);
    private static final Field<Integer> C_SORT_ORDER = field("c.sort_order", Integer.class);

    private static final Table<?> PRODUCTS = table("products").as("p");
    private static final Field<UUID> P_ID = field("p.id", UUID.class);
    private static final Field<String> P_SKU = field("p.sku", String.class);
    private static final Field<String> P_NAME = field("p.name", String.class);
    private static final Field<String> P_DESCRIPTION = field("p.description", String.class);
    private static final Field<BigDecimal> P_PRICE = field("p.price", BigDecimal.class);
    private static final Field<String> P_CURRENCY = field("p.currency", String.class);
    private static final Field<Integer> P_STOCK = field("p.stock_quantity", Integer.class);
    private static final Field<UUID> P_CATEGORY_ID = field("p.category_id", UUID.class);
    private static final Field<String> P_IMAGE_URLS = field("p.image_urls", String.class);
    private static final Field<String> P_STATUS = field("p.status", String.class);
    private static final Field<LocalDateTime> P_CREATED_AT = field("p.created_at", LocalDateTime.class);
    private static final Field<LocalDateTime> P_UPDATED_AT = field("p.updated_at", LocalDateTime.class);

    private final DSLContext dsl;

    public ReportJooqQueryAdapterImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<Category> findAllCategories() {
        return dsl.select(C_ID, C_NAME, C_SLUG, C_PARENT_ID, C_SORT_ORDER)
                .from(CATEGORIES)
                .orderBy(C_SORT_ORDER, C_NAME)
                .fetch(this::toCategory);
    }

    @Override
    public List<ProductWithCategory> findAllProductsWithCategory() {
        return dsl.select(P_ID, P_SKU, P_NAME, P_DESCRIPTION, P_PRICE, P_CURRENCY, P_STOCK,
                        P_CATEGORY_ID, P_IMAGE_URLS, P_STATUS, P_CREATED_AT, P_UPDATED_AT,
                        C_ID, C_NAME, C_SLUG, C_PARENT_ID, C_SORT_ORDER)
                .from(PRODUCTS)
                .leftJoin(CATEGORIES).on(P_CATEGORY_ID.eq(C_ID))
                .orderBy(P_NAME)
                .fetch(r -> new ProductWithCategory(
                        toProduct(r),
                        r.get(C_ID) == null ? null : toCategory(r)));
    }

    private Category toCategory(Record r) {
        return new Category(r.get(C_ID), r.get(C_NAME), r.get(C_SLUG),
                r.get(C_PARENT_ID), r.get(C_SORT_ORDER));
    }

    private Product toProduct(Record r) {
        return new Product(
                r.get(P_ID), r.get(P_SKU), r.get(P_NAME), r.get(P_DESCRIPTION),
                r.get(P_PRICE), r.get(P_CURRENCY), r.get(P_STOCK),
                r.get(P_CATEGORY_ID), parseImageUrls(r.get(P_IMAGE_URLS)),
                ProductStatus.valueOf(r.get(P_STATUS)),
                r.get(P_CREATED_AT).toInstant(ZoneOffset.UTC),
                r.get(P_UPDATED_AT).toInstant(ZoneOffset.UTC));
    }

    private List<ImageUrl> parseImageUrls(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<ImageUrl>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not deserialize imageUrls from JSON: " + json, e);
        }
    }
}
