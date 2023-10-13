package com.faesa.librarycli.core.registerpatron;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatronRepositoryJDBC implements PatronRepository {

    private final Connection connection;

    @Override
    public Patron save(Patron entity) {
        String sql = "INSERT INTO patron (name, type) VALUES (?, ?)";
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            entity.setStatementValues(statement);
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.assignId(generatedKeys.getLong(1));
            }
            connection.commit();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
