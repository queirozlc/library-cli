package com.faesa.librarycli.core.placinghold;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HoldRepositoryJDBC implements HoldRepository {

    private final Connection connection;

    @Override
    public Hold save(Hold entity) {
        if (entity.hasId()) {
            return updateExistent(entity);
        }
        String sql = "INSERT INTO hold (patron_id, instance_id, date_placed, days_to_expire, hold_fee) VALUES (?, ?, ?, ?, ?)";
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
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Hold> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public Collection<Hold> findAll() {
        return null;
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    private Hold updateExistent(Hold entity) {
        String sql = "UPDATE hold SET patron_id = ?, instance_id = ?, date_placed = ?, days_to_expire = ?, hold_fee = ? WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            entity.setStatementValues(statement);
            statement.setLong(6, entity.getId());
            statement.executeUpdate();
            connection.commit();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
