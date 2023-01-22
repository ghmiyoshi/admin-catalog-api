package com.fullcycle.admin.catalogo.application.application.category.update;

import com.fullcycle.admin.catalogo.application.category.update.DefaultUpdateCategoryUseCase;
import com.fullcycle.admin.catalogo.application.category.update.UpdateCategoryCommand;
import com.fullcycle.catalogo.domain.category.Category;
import com.fullcycle.catalogo.domain.category.CategoryGateway;
import com.fullcycle.catalogo.domain.category.CategoryID;
import com.fullcycle.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    // 1. Teste do caminho feliz
    // 2. Teste passando uma propriedade invalida (name)
    // 3. Teste atualizando uma categoria para inativa
    // 4. Teste simulando um erro generico vindo do gateway
    // 5. Teste atualizar categoria passando ID invalido

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var aCategory = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(), expectedName,
                expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand).get();
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(argThat(aUpdateCategory ->
                Objects.equals(expectedName, aUpdateCategory.getName())
                        && Objects.equals(expectedId, aUpdateCategory.getId())
                        && Objects.equals(expectedIsActive, aUpdateCategory.isActive())
                        && Objects.equals(expectedDescription, aUpdateCategory.getDescription())
                        && Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt())
                        && aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt())
                        && isNull(aUpdateCategory.getDeletedAt())
        ));
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var aCategory = Category.newCategory("Film", null, true);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(),
                expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());
        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(),
                expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        final var actualOutput = useCase.execute(aCommand).get();
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(argThat(aUpdateCategory ->
                Objects.equals(expectedName, aUpdateCategory.getName())
                        && Objects.equals(expectedId, aUpdateCategory.getId())
                        && Objects.equals(expectedIsActive, aUpdateCategory.isActive())
                        && Objects.equals(expectedDescription, aUpdateCategory.getDescription())
                        && Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt())
                        && aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt())
                        && nonNull(aUpdateCategory.getDeletedAt())
        ));
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var aCategory = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();
        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(),
                expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));
        when(categoryGateway.update(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());

        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(argThat(aUpdateCategory ->
                Objects.equals(expectedName, aUpdateCategory.getName())
                        && Objects.equals(expectedId, aUpdateCategory.getId())
                        && Objects.equals(expectedIsActive, aUpdateCategory.isActive())
                        && Objects.equals(expectedDescription, aUpdateCategory.getDescription())
                        && Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt())
                        && aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt())
                        && isNull(aUpdateCategory.getDeletedAt())
        ));
    }

    @Test
    void givenAInvalidIDCommandWithInvalidID_Name_whenCallsUpdateCategory_ShouldReturnNotFoundException() {
        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = "123";

        final var expectedErrorMessage = "Category with ID 123 was not found";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(expectedId,
                expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(CategoryID.from(expectedId)))).thenReturn(Optional.empty());

        final var actualException = assertThrows(DomainException.class,
                () -> useCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        verify(categoryGateway, times(1)).findById(eq(CategoryID.from(expectedId)));
        verify(categoryGateway, times(0)).update(any());
    }

}
