package com.fullcycle.catalogo.domain.category;

public record CategorySearchQuery<T>(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction
) {
}
