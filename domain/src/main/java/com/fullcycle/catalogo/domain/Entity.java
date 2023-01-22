package com.fullcycle.catalogo.domain;

import com.fullcycle.catalogo.domain.validation.ValidationHandler;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class Entity<ID extends Identifier> {

    protected final ID id;

    public abstract void validate(ValidationHandler handler);

    protected Entity(ID id) {
        requireNonNull(id, "'id' should not be null");
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
