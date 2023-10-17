package com.faesa.librarycli.core.createauthor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
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
        var sql = "INSERT INTO C##LABDATABASE.author (name, nationality) VALUES (?, ?)";
        try (var statement = connection.prepareStatement(sql, new String[]{"id"})) {
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
    public Optional<Author> findById(Long id) {
        String sql = "SELECT * FROM C##LABDATABASE.author WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Constructor<Author> authorConstructor = Author.class.getDeclaredConstructor();
                authorConstructor.setAccessible(true);
                var author = authorConstructor.newInstance();
                author.fromResultSet(resultSet);
                return Optional.of(author);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        var sql = "DELETE FROM C##LABDATABASE.author WHERE id = ?";
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
        var sql = "SELECT * FROM C##LABDATABASE.author";
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
        String sql = "SELECT COUNT(*) FROM C##LABDATABASE.author WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
