package com.faesa.librarycli.core.registerpatron;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class PatronRepositoryJDBC implements PatronRepository {
    @Override
    public Patron save(Patron entity) {
        return null;
    }

    @Override
    public Optional<Patron> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public Collection<Patron> findAll() {
        return null;
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }
}
