package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.catalogo.domain.category.CategoryGateway;
import com.fullcycle.catalogo.domain.category.CategoryID;
import com.fullcycle.catalogo.domain.exceptions.DomainException;
import com.fullcycle.catalogo.domain.validation.Error;

import java.util.function.Supplier;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public CategoryOutput execute(String anIn) {
        final var categoryID = CategoryID.from(anIn);
        return this.categoryGateway.findById(categoryID)
                .map(CategoryOutput::from)
                .orElseThrow(notFound(categoryID));
    }

    private Supplier<DomainException> notFound(final CategoryID anId) {
        return () -> DomainException.with(new Error("Category with ID %s was not found"
                .formatted(anId.getValue())));
    }

}
