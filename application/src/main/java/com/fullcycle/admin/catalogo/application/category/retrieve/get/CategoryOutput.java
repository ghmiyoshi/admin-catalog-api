package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.catalogo.domain.category.Category;
import com.fullcycle.catalogo.domain.category.CategoryID;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record CategoryOutput(
        CategoryID id,
        String name,
        String description,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public static CategoryOutput from(final Category aCategory) {
        return new CategoryOutput(aCategory.getId(),
                                  aCategory.getName(),
                                  aCategory.getDescription(),
                                  aCategory.isActive(),
                                  aCategory.getCreatedAt().plusNanos(500).truncatedTo(ChronoUnit.MICROS),
                                  aCategory.getUpdatedAt().plusNanos(500).truncatedTo(ChronoUnit.MICROS),
                                  aCategory.getDeletedAt());
    }

}
