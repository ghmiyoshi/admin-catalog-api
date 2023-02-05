package com.fullcycle.admin.catalogo.infrastructure;

import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalogo.infrastructure.configuration.WebServerConfig;
import com.fullcycle.catalogo.domain.category.Category;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;

import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "development");
        SpringApplication.run(WebServerConfig.class, args);
    }

//    @Bean
//    public ApplicationRunner runner(CategoryRepository repository) {
//        return args -> {
//            List<CategoryJpaEntity> categories = repository.findAll();
//
//            Category category = Category.newCategory("teste", "teste insercao", true);
//            repository.saveAndFlush(CategoryJpaEntity.from(category));
//
//            repository.deleteAll();
//        };
//    }

}
