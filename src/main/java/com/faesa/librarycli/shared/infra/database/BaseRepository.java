package com.faesa.librarycli.shared.infra.database;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public interface Repository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    void delete(T entity);

    void deleteById(ID id);

    Collection<T> findAll();

    boolean existsById(ID id);
}
