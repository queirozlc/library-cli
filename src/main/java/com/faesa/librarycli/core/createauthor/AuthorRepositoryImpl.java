package com.faesa.librarycli.core.createauthor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AuthorRepositoryImpl implements AuthorRepository {

    private final Connection connection;

    @Override
    public Author save(Author entity) {
        var sql = "INSERT INTO author (name, nationality) VALUES (?, ?)";
        try (var statement = connection.prepareStatement(sql)) {
            entity.setStatementValues(statement);
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.assignId(generatedKeys.getLong(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Author> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        var sql = "DELETE FROM author WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Author> findAll() {
        var authors = new ArrayList<Author>();
        var sql = "SELECT * FROM author";
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var author = new Author();
                author.fromResultSet(resultSet);
                authors.add(author);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authors;
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
