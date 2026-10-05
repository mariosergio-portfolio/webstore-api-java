package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.webstore.application.port.out.ReportQueryRepository;
import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.City;
import com.mycompany.webstore.domain.model.CityDistance;
import com.mycompany.webstore.domain.model.ImageUrl;
import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import com.mycompany.webstore.domain.model.Supplier;
import com.mycompany.webstore.shared.exception.BusinessRuleException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jooq.impl.DSL.condition;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.val;

@Component
public class ReportJooqQueryAdapterImpl implements ReportQueryRepository {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Table<?> CATEGORIES = table("categories c");
    private static final Field<UUID> C_ID = field("c.id", UUID.class);
    private static final Field<String> C_NAME = field("c.name", String.class);
    private static final Field<String> C_SLUG = field("c.slug", String.class);
    private static final Field<UUID> C_PARENT_ID = field("c.parent_id", UUID.class);
    private static final Field<Integer> C_SORT_ORDER = field("c.sort_order", Integer.class);

    private static final Table<?> PRODUCTS = table("products p");
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

    private static final Table<?> SUPPLIERS = table("suppliers s");
    private static final Table<?> CITIES = table("cities ci");
    private static final Field<UUID> S_ID = field("s.id", UUID.class);
    private static final Field<String> S_NAME = field("s.name", String.class);
    private static final Field<String> S_EMAIL = field("s.email", String.class);
    private static final Field<UUID> S_CITY_ID = field("s.address_city_id", UUID.class);
    private static final Field<UUID> P_SUPPLIER_ID = field("p.supplier_id", UUID.class);
    private static final Field<UUID> CI_ID = field("ci.id", UUID.class);
    private static final Field<String> CI_NAME = field("ci.name", String.class);
    private static final Field<String> CI_STATE = field("ci.state", String.class);
    private static final Field<String> CI_COUNTRY = field("ci.country", String.class);

    private static final Pattern WKT_POINT =
            Pattern.compile("POINT\\s*\\(\\s*(-?[0-9.eE+-]+)\\s+(-?[0-9.eE+-]+)\\s*\\)");

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

    @Override
    public List<Supplier> findAllSuppliers() {

        Field<String> location = cityLocation();

        Field<Integer> productCount = count(P_ID);

        return dsl.select(S_ID, S_NAME, S_EMAIL, CI_ID, CI_NAME, CI_STATE, CI_COUNTRY, location, productCount)
                .from(SUPPLIERS)
                .join(CITIES).on(S_CITY_ID.eq(CI_ID))
                .leftJoin(PRODUCTS).on(P_SUPPLIER_ID.eq(S_ID))
                .groupBy(S_ID, S_NAME, S_EMAIL, CI_ID, CI_NAME, CI_STATE, CI_COUNTRY, location)
                .orderBy(S_NAME)
                .fetch(r -> {
                    double[] lonLat = parsePoint(r.get(location));
                    City city = new City(r.get(CI_ID), r.get(CI_NAME), r.get(CI_STATE),
                            r.get(CI_COUNTRY), lonLat[1], lonLat[0]);
                    return new Supplier(r.get(S_ID), r.get(S_NAME), r.get(S_EMAIL), city, r.get(productCount));
                });
    }

    @Override
    public List<City> findAllCities() {
        Field<String> location = cityLocation();
        return dsl.select(CI_ID, CI_NAME, CI_STATE, CI_COUNTRY, location)
                .from(CITIES)
                .orderBy(CI_COUNTRY, CI_NAME)
                .fetch(r -> {
                    double[] lonLat = parsePoint(r.get(location));
                    return new City(r.get(CI_ID), r.get(CI_NAME), r.get(CI_STATE),
                            r.get(CI_COUNTRY), lonLat[1], lonLat[0]);
                });
    }

    @Override
    public List<City> findCitiesByName(String name, String country) {
        Field<String> location = cityLocation();
        Condition condition = field("lower(ci.name)", String.class).eq(name.toLowerCase());
        if (country != null) {
            condition = condition.and(field("lower(ci.country)", String.class).eq(country.toLowerCase()));
        }
        return dsl.select(CI_ID, CI_NAME, CI_STATE, CI_COUNTRY, location)
                .from(CITIES)
                .where(condition)
                .fetch(r -> toCity(r, location));
    }

    @Override
    public List<CityDistance> findCitiesWithinKm(City origin, double radiusKm) {
        Field<String> location = cityLocation();
        if (dsl.dialect().family() != SQLDialect.POSTGRES) {
            throw new BusinessRuleException("Nearby-city search requires PostgreSQL with PostGIS");
        }
        // Filtering, distance and ordering are all done by PostGIS on the WGS 84 spheroid;
        // ST_DWithin uses the GIST index on cities.location.
        String originPoint = "public.geography(public.ST_SetSRID(public.ST_MakePoint({0}, {1}), 4326))";
        Field<Double> distanceKm = field(
                "public.ST_Distance(public.geography(ci.location), " + originPoint + ") / 1000.0",
                Double.class, val(origin.longitude()), val(origin.latitude()));
        Condition within = condition(
                "public.ST_DWithin(public.geography(ci.location), " + originPoint + ", {2})",
                val(origin.longitude()), val(origin.latitude()), val(radiusKm * 1000.0));
        return dsl.select(CI_ID, CI_NAME, CI_STATE, CI_COUNTRY, location, distanceKm)
                .from(CITIES)
                .where(within.and(CI_ID.ne(origin.id())))
                .orderBy(distanceKm)
                .fetch(r -> new CityDistance(toCity(r, location), r.get(distanceKm)));
    }

    private City toCity(Record r, Field<String> location) {
        double[] lonLat = parsePoint(r.get(location));
        return new City(r.get(CI_ID), r.get(CI_NAME), r.get(CI_STATE), r.get(CI_COUNTRY), lonLat[1], lonLat[0]);
    }

    /**
     * PostgreSQL stores cities.location as a PostGIS geometry (read as WKT via ST_AsText);
     * the H2 dev schema stores the WKT text directly.
     */
    private Field<String> cityLocation() {
        return field(
                dsl.dialect().family() == SQLDialect.POSTGRES ? "public.ST_AsText(ci.location)" : "ci.location",
                String.class);
    }

    /** Parses WKT {@code POINT(lon lat)} into {@code [lon, lat]}. */
    private static double[] parsePoint(String wkt) {
        Matcher m = WKT_POINT.matcher(wkt == null ? "" : wkt);
        if (!m.matches()) {
            throw new IllegalArgumentException("Unexpected city location: " + wkt);
        }
        return new double[]{Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))};
    }
}
