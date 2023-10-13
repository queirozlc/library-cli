package com.faesa.librarycli.shared.infra.database;

import java.util.Collection;
import java.util.Optional;

public interface BaseRepository<T extends DomainValuesExtractor<ID>, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    void deleteById(ID id);

    Collection<T> findAll();

    boolean existsById(ID id);
}
