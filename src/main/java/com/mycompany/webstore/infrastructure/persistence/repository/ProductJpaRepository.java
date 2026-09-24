package com.mycompany.webstore.infrastructure.persistence.repository;

import com.mycompany.webstore.domain.model.ProductStatus;
import com.mycompany.webstore.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {

    Optional<ProductJpaEntity> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsByCategoryId(UUID categoryId);

    @Query("""
            SELECT p FROM ProductJpaEntity p
            WHERE (CAST(:query AS string) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                                                  OR LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')))
              AND (CAST(:categoryId AS java.util.UUID) IS NULL OR p.categoryId = :categoryId)
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (CAST(:status AS string) IS NULL OR p.status = :status)
            """)
    Page<ProductJpaEntity> findAllFiltered(@Param("query") String query,
                                           @Param("categoryId") UUID categoryId,
                                           @Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice,
                                           @Param("status") ProductStatus status,
                                           Pageable pageable);
}
