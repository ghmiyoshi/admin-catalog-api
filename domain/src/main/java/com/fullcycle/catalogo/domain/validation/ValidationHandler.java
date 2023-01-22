package com.fullcycle.catalogo.domain.validation;

import java.util.List;

import static java.util.Objects.nonNull;

public interface ValidationHandler {

    ValidationHandler append(Error anError);

    ValidationHandler append(ValidationHandler anHandler);

    ValidationHandler validate(Validation aValidation);

    List<Error> getErrors();

    default boolean hasError() {
        return nonNull(getErrors()) && !getErrors().isEmpty();
    }

    default Error firstError() {
        if (hasError()) {
            return getErrors().get(0);
        } else {
            return null;
        }
    }

    interface Validation {
        void validate();
    }
}
