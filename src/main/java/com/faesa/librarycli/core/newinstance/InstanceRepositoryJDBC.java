package com.faesa.librarycli.core.newinstance;

import com.faesa.librarycli.core.createauthor.Author;
import com.faesa.librarycli.core.createbook.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        String sql = "INSERT INTO C##LABDATABASE.instance (status, type, book_isbn) VALUES (?, ?, ?)";

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
    public Optional<Instance> findById(Long id) {
        final var sql = getFindByPredicateQuery("I.ID = ?");
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(fromResultSet(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        final var sql = "DELETE FROM C##LABDATABASE.instance WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            statement.setLong(1, id);
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Instance> findAll() {
        final var sql = getFindAllQuery();
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            return fromResultSetList(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<Instance> fromResultSetList(ResultSet resultSet) {
        final var instances = new ArrayList<>(Collections.<Instance>emptyList());
        try {
            var instanceId = resultSet.getLong("instance_id");
            final var instanceStatus = InstanceStatus.valueOf(resultSet.getString("status"));
            final var instanceType = InstanceType.supports(resultSet.getString("type"));
            var bookIsbn = resultSet.getString("book_isbn");
            var bookTitle = resultSet.getString("title");
            var bookPages = resultSet.getInt("pages");
            final var bookPublicationDate = resultSet.getDate("publication_date").toLocalDate();
            final var bookId = resultSet.getLong("book_id");
            final var authorName = resultSet.getString("name");
            final var authorNationality = resultSet.getString("nationality");
            final var authorId = resultSet.getLong("author_id");
            final var author = new Author(authorName, authorNationality);
            author.assignId(authorId);
            final var book = new Book(bookTitle, bookIsbn, bookPublicationDate, bookPages, author);
            book.assignId(bookId);
            final var instance = new Instance(instanceStatus, instanceType, book);
            instance.assignId(instanceId);
            instances.add(instance);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return instances;
    }

    private Instance fromResultSet(ResultSet resultSet) {
        try {
            var instanceId = resultSet.getLong("instance_id");
            final var instanceStatus = InstanceStatus.valueOf(resultSet.getString("status"));
            final var instanceType = InstanceType.supports(resultSet.getString("type"));
            var bookIsbn = resultSet.getString("book_isbn");
            var bookTitle = resultSet.getString("title");
            var bookPages = resultSet.getInt("pages");
            final var bookPublicationDate = resultSet.getDate("publication_date").toLocalDate();
            final var bookId = resultSet.getLong("book_id");
            final var authorName = resultSet.getString("name");
            final var authorNationality = resultSet.getString("nationality");
            final var authorId = resultSet.getLong("author_id");
            final var author = new Author(authorName, authorNationality);
            author.assignId(authorId);
            final var book = new Book(bookTitle, bookIsbn, bookPublicationDate, bookPages, author);
            book.assignId(bookId);
            final var instance = new Instance(instanceStatus, instanceType, book);
            instance.assignId(instanceId);
            return instance;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFindAllQuery() {
        return """
                   SELECT
                       I.ID AS INSTANCE_ID,
                       I.BOOK_ISBN,
                       I.STATUS,
                       I.TYPE,
                       B.TITLE,
                       B.PAGES,
                       B.PUBLICATION_DATE,
                       B.ID AS BOOK_ID,
                       A.NAME,
                       A.NATIONALITY,
                       A.ID AS AUTHOR_ID
                   FROM C##LABDATABASE.instance I
                   INNER JOIN C##LABDATABASE.BOOK B on I.BOOK_ISBN = B.ISBN
                   INNER JOIN C##LABDATABASE.AUTHOR A on B.AUTHOR_ID = A.ID;
                """;
    }

    private String getFindByPredicateQuery(String predicate) {
        return """
                   SELECT
                       I.ID AS INSTANCE_ID,
                       I.BOOK_ISBN,
                       I.STATUS,
                       I.TYPE,
                       B.TITLE,
                       B.PAGES,
                       B.PUBLICATION_DATE,
                       B.ID AS BOOK_ID,
                       A.NAME,
                       A.NATIONALITY,
                       A.ID AS AUTHOR_ID
                   FROM C##LABDATABASE.instance I
                   INNER JOIN C##LABDATABASE.BOOK B on I.BOOK_ISBN = B.ISBN
                   INNER JOIN C##LABDATABASE.AUTHOR A on B.AUTHOR_ID = A.ID;
                   WHERE %s;
                """.formatted(predicate);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM C##LABDATABASE.instance WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int count() {
        final var sql = "SELECT COUNT(*) FROM C##LABDATABASE.instance";
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }


    private Instance update(Instance entity) {
        String sql = "UPDATE C##LABDATABASE.instance SET status = ?, type = ?, book_isbn = ? WHERE id = ?";

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
