package com.mycompany.webstore.application.service;

import com.mycompany.webstore.application.port.out.CategoryRepository;
import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.shared.exception.BusinessRuleException;
import com.mycompany.webstore.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryPortImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryPortImpl categoryPort;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category(UUID.randomUUID(), "Electronics", "electronics", null, 1);
    }

    // ── getCategoryById ───────────────────────────────────────────────────────

    @Test
    void should_return_category_when_id_exists() {
        // Arrange
        when(categoryRepository.findById(sampleCategory.getId())).thenReturn(Optional.of(sampleCategory));

        // Act
        Category result = categoryPort.getCategoryById(sampleCategory.getId());

        // Assert
        assertThat(result).isEqualTo(sampleCategory);
    }

    @Test
    void should_throw_not_found_when_category_id_does_not_exist() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(categoryRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryPort.getCategoryById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    // ── createCategory ────────────────────────────────────────────────────────

    @Test
    void should_create_category_when_slug_is_unique() {
        // Arrange
        Category input = new Category(null, "Books", "books", null, 2);
        when(categoryRepository.existsBySlug("books")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Category result = categoryPort.createCategory(input);

        // Assert
        assertThat(result.getId()).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void should_throw_business_rule_when_slug_already_exists() {
        // Arrange
        Category input = new Category(null, "Duplicate", "electronics", null, 0);
        when(categoryRepository.existsBySlug("electronics")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryPort.createCategory(input))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("slug already exists");
    }

    // ── deleteCategory ────────────────────────────────────────────────────────

    @Test
    void should_delete_category_when_no_products_assigned() {
        // Arrange
        when(categoryRepository.findById(sampleCategory.getId())).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.hasProducts(sampleCategory.getId())).thenReturn(false);

        // Act
        categoryPort.deleteCategory(sampleCategory.getId());

        // Assert
        verify(categoryRepository).deleteById(sampleCategory.getId());
    }

    @Test
    void should_throw_business_rule_when_deleting_category_with_products() {
        // Arrange
        when(categoryRepository.findById(sampleCategory.getId())).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.hasProducts(sampleCategory.getId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryPort.deleteCategory(sampleCategory.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot delete category");
        verify(categoryRepository, never()).deleteById(any());
    }

    // ── updateCategory ────────────────────────────────────────────────────────

    @Test
    void should_throw_business_rule_when_category_is_own_parent() {
        // Arrange
        Category update = new Category(null, "Electronics", "electronics", sampleCategory.getId(), 1);
        when(categoryRepository.findById(sampleCategory.getId())).thenReturn(Optional.of(sampleCategory));

        // Act & Assert
        assertThatThrownBy(() -> categoryPort.updateCategory(sampleCategory.getId(), update))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cannot be its own parent");
    }
}
