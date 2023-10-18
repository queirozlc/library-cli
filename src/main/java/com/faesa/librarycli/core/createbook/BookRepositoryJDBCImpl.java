package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.core.createauthor.Author;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.newinstance.InstanceStatus;
import com.faesa.librarycli.core.newinstance.InstanceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookRepositoryJDBCImpl implements BookRepository {

    private final Connection connection;

    @Override
    public Book save(Book entity) {
        try (var statement = connection.prepareStatement("INSERT INTO C##LABDATABASE.book (title, isbn, publication_date, pages, author_id) VALUES (?, ?, ?, ?, ?)", new String[]{"id"})) {
            entity.setStatementValues(statement);
            statement.execute();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.assignId(generatedKeys.getLong(1));
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = getFindQueryWithPredicate("b.id = ?");
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
        String sql = "DELETE FROM C##LABDATABASE.book WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Book> findAll() {
        var books = new ArrayList<>(Collections.<Book>emptyList());
        String sql = getFindAllQuery();

        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                books.add(fromResultSet(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM C##LABDATABASE.book WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public int count() {
        final var sql = "SELECT COUNT(*) FROM C##LABDATABASE.book";
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


    @Override
    public Optional<Book> findByIsbn(String bookIsbn) {
        String sql = getFindQueryWithPredicate("b.isbn = ?");

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, bookIsbn);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(fromResultSet(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    private Book fromResultSet(ResultSet resultSet) {
        try {
            var bookPublicationDate = resultSet.getDate("publication_date").toLocalDate();

            var author = new Author(
                    resultSet.getString("name"),
                    resultSet.getString("nationality")
            );
            author.assignId(resultSet.getLong("author_id"));
            var book = new Book(
                    resultSet.getString("title"),
                    resultSet.getString("isbn"),
                    bookPublicationDate,
                    resultSet.getInt("pages"),
                    author
            );
            book.assignId(resultSet.getLong("id"));
            this.getInstances(book);
            return book;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void getInstances(Book book) {
        String sql = getInstancesQuery();
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getIsbn());
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var instanceType = InstanceType.supports(resultSet.getString("type"));
                var instanceStatus = InstanceStatus.valueOf(resultSet.getString("status"));
                var instance = new Instance(instanceStatus, instanceType, book);
                instance.assignId(resultSet.getLong("instance_id"));
                book.addInstance(instance);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFindQueryWithPredicate(String predicate) {
        return "SELECT b.*, a.name, a.nationality " +
                "FROM C##LABDATABASE.book b " +
                "INNER JOIN C##LABDATABASE.author a ON b.author_id = a.id " +
                "WHERE " + predicate + " " +
                "ORDER BY b.title";

    }

    private String getFindAllQuery() {
        return "SELECT b.* " +
                "FROM C##LABDATABASE.book b " +
                "INNER JOIN C##LABDATABASE.author a ON b.author_id = a.id " +
                "ORDER BY b.title";
    }

    private String getInstancesQuery() {
        return "SELECT i.id as instance_id, i.type, i.status " +
                "FROM C##LABDATABASE.instance i " +
                "WHERE i.book_isbn = ?";
    }
}
