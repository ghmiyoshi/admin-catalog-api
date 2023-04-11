package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.catalogo.domain.category.Category;
import com.fullcycle.catalogo.domain.category.CategorySearchQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class ListCategoriesUseCaseIT {

    @Autowired
    private ListCategoriesUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void mockup() {
        final var categories = Stream.of(Category.newCategory("Filmes", null, true),
                                         Category.newCategory("Netflix", "Títulos de autoria da Netflix", true),
                                         Category.newCategory("Amazon", "Títulos de autoria da Amazon Prime", true),
                                         Category.newCategory("Documentários", null, true),
                                         Category.newCategory("Sports", null, true),
                                         Category.newCategory("Kids", "Categoria para criancas", true),
                                         Category.newCategory("Series", null, true))
                .map(CategoryJpaEntity::from)
                .toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    @Test
    public void givenAValidTerm_whenTermDoesntMatchsPrePersisted_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "fdjfhdjkahjk shaudhua";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage,
                                                   expectedTerms, expectedSort,
                                                   expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
    }

    @ParameterizedTest
    @CsvSource({
            "fil, 0, 10, 1, 1, Filmes",
            "net, 0, 10, 1, 1, Netflix",
            "ZON, 0, 10, 1, 1, Amazon",
            "KI, 0, 10, 1, 1, Kids",
            "criancas, 0, 10, 1, 1, Kids",
            "da Amazon, 0, 10, 1, 1, Amazon",
    })
    public void givenAValidTerm_whenCallListCategories_shouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage,
                                                   expectedTerms, expectedSort,
                                                   expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }


    @ParameterizedTest
    @CsvSource({
            "name, asc, 0, 10, 7, 7, Amazon",
            "name, desc, 0, 10, 7, 7, Sports",
            //"createdAt, asc, 0, 10, 7, 7, Filmes",
            //"createdAt, desc, 0, 10, 7, 7, Series"
    })
    public void givenAValidSortAndDirection_whenCallListCategories_thenShouldReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName) {
        final var expectedTerms = "";

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage,
                                                   expectedTerms, expectedSort,
                                                   expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2, 2, 7, Amazon;Documentários",
            "1, 2, 2, 7, Filmes;Kids",
            "2, 2, 2, 7, Netflix;Series",
            "3, 2, 1, 7, Sports",
    })
    public void givenAValidPage_whenCallsListCategories_shouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTerms = "";

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage,
                                                   expectedTerms, expectedSort,
                                                   expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());

        var index = 0;
        for (String expectedName : expectedCategoriesName.split(";")) {
            assertEquals(expectedName, actualResult.items().get(index).name());
            index++;
        }
    }

}
