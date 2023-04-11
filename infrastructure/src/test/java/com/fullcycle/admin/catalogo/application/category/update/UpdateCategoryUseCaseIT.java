package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.catalogo.domain.category.Category;
import com.fullcycle.catalogo.domain.category.CategoryGateway;
import com.fullcycle.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
public class UpdateCategoryUseCaseIT {

    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() throws InterruptedException {
        final var aCategory = Category.newCategory("Film", null, true);
        save(aCategory);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(), expectedName,
                                                        expectedDescription, expectedIsActive);

        assertEquals(1, categoryRepository.count());

        Thread.sleep(1000L);
        final var actualOutput = useCase.execute(aCommand).get();
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        final var actualCategory =
                categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt().toEpochMilli(), actualCategory.getCreatedAt().toEpochMilli());
        assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var aCategory = Category.newCategory("Film", null, true);
        save(aCategory);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;


        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(),
                                                        expectedName, expectedDescription, expectedIsActive);

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());
        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() throws InterruptedException {
        final var aCategory = Category.newCategory("Film", null, true);
        save(aCategory);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(),
                                                        expectedName, expectedDescription, expectedIsActive);

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        Thread.sleep(1);
        final var actualOutput = useCase.execute(aCommand).get();
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        final var actualCategory =
                categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt().toEpochMilli(), actualCategory.getCreatedAt().toEpochMilli());
        assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var aCategory = Category.newCategory("Film", null, true);
        save(aCategory);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();
        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(),
                                                        expectedName, expectedDescription, expectedIsActive);

        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).update(any());

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(aCategory.getName(), actualCategory.getName());
        assertEquals(aCategory.getDescription(), actualCategory.getDescription());
        assertEquals(aCategory.isActive(), actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt().toEpochMilli(), actualCategory.getCreatedAt().toEpochMilli());
        assertEquals(aCategory.getUpdatedAt().toEpochMilli(), actualCategory.getUpdatedAt().toEpochMilli());
        assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidIDCommandWithInvalidID_whenCallsUpdateCategory_ShouldReturnNotFoundException() {
        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = "123";

        final var expectedErrorMessage = "Category with ID 123 was not found";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(expectedId,
                                                        expectedName, expectedDescription, expectedIsActive);

        final var actualException = assertThrows(DomainException.class,
                                                 () -> useCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    private void save(final Category... aCategory) {
        categoryRepository.saveAllAndFlush(List.of(aCategory).stream().map(CategoryJpaEntity::from).toList());
    }

}
