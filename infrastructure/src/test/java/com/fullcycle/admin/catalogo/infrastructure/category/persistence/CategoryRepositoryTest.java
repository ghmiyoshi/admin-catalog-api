package com.fullcycle.admin.catalogo.infrastructure.category.persistence;

import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.catalogo.domain.category.Category;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenAnInvalidNullName_whenCallSave_shouldReturnError() {
        final var expectedProperty = "name";
        final var expectedMessage = "not-null property references a null or transient value : " +
                "com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.name";
        final var aCategory = Category.newCategory("Filmes", "O mais assistido", true);
        final var anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setName(null);

        DataIntegrityViolationException actualException = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(anEntity));
        PropertyValueException actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedProperty, actualCause.getPropertyName());
        assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidCreatedAt_whenCallSave_shouldReturnError() {
        final var expectedProperty = "createdAt";
        final var expectedMessage = "not-null property references a null or transient value : " +
                "com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.createdAt";
        final var aCategory = Category.newCategory("Filmes", "O mais assistido", true);
        final var anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setCreatedAt(null);

        DataIntegrityViolationException actualException = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(anEntity));
        PropertyValueException actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedProperty, actualCause.getPropertyName());
        assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidUpdateAt_whenCallSave_shouldReturnError() {
        final var expectedProperty = "updatedAt";
        final var expectedMessage = "not-null property references a null or transient value : " +
                "com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";
        final var aCategory = Category.newCategory("Filmes", "O mais assistido", true);
        final var anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setUpdatedAt(null);

        DataIntegrityViolationException actualException = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(anEntity));
        PropertyValueException actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedProperty, actualCause.getPropertyName());
        assertEquals(expectedMessage, actualCause.getMessage());
    }

}
