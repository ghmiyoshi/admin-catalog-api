package com.fullcycle.admin.catalogo.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.AbstractEnvironment;

import static com.fullcycle.admin.catalogo.infrastructure.Main.main;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainTest {

    @Test
    void testMain() {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "test");
        assertNotNull(new Main());
        main(new String[]{});
    }

}
