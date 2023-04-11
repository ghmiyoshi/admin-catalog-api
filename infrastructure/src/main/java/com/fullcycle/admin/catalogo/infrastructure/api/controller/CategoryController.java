package com.fullcycle.admin.catalogo.infrastructure.api.controller;

import com.fullcycle.admin.catalogo.infrastructure.api.CategoryAPI;
import com.fullcycle.catalogo.domain.pagination.Pagination;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController implements CategoryAPI {

    @Override
    public ResponseEntity<?> createCategory() {
        return null;
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

}
