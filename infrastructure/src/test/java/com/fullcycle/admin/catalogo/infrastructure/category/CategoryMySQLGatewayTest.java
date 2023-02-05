package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.infrastructure.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.catalogo.domain.category.Category;
import com.fullcycle.catalogo.domain.category.CategoryID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.create(aCategory);

        assertEquals(1, categoryRepository.count());
        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        assertNull(actualCategory.getDeletedAt());

        var actualCategoryEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), actualCategoryEntity.getId());
        assertEquals(expectedName, actualCategoryEntity.getName());
        assertEquals(expectedDescription, actualCategoryEntity.getDescription());
        assertEquals(expectedIsActive, actualCategoryEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), actualCategoryEntity.getCreatedAt());
        assertNull(actualCategoryEntity.getDeletedAt());
    }

    @Test
    void givenAValidCategory_whenCallsUpdate_shouldReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("Film", null, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        final var aUpdateCategory = aCategory.clone().update(expectedName, expectedDescription, expectedIsActive);
        final var actualCategory = categoryGateway.update(aUpdateCategory);

        assertEquals(1, categoryRepository.count());
        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        assertNull(actualCategory.getDeletedAt());

        var actualCategoryEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), actualCategoryEntity.getId());
        assertEquals(expectedName, actualCategoryEntity.getName());
        assertEquals(expectedDescription, actualCategoryEntity.getDescription());
        assertEquals(expectedIsActive, actualCategoryEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), actualCategoryEntity.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        assertNull(actualCategoryEntity.getDeletedAt());
    }

    @Test
    void givenAPrePersistedCategory_whenTryToDeletedIt_shouldDeleteCategory() {
        final var aCategory = Category.newCategory("Filmes", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(aCategory.getId());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenInvalidCategoryId_whenTryToDeleteCategory() {
        assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.from("invalid"));

        assertEquals(0, categoryRepository.count());
    }

}
