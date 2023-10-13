package com.faesa.librarycli.core.newinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InstanceRepositoryJDBC implements InstanceRepository {

    private final Connection connection;

    @Override
    public Instance save(Instance entity) {
        return null;
    }

    @Override
    public Optional<Instance> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public Collection<Instance> findAll() {
        return null;
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }
}
