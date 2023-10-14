package com.faesa.librarycli.core.newinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InstanceRepositoryJDBC implements InstanceRepository {

    private final Connection connection;

    @Override
    public Instance save(Instance entity) {
        if (entity.hasId()) {
            return update(entity);
        }
        String sql = "INSERT INTO instance (status, type, book_isbn) VALUES (?, ?, ?)";

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private Instance update(Instance entity) {
        String sql = "UPDATE instance SET status = ?, type = ?, book_isbn = ? WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            entity.setStatementValues(statement);
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            connection.commit();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
