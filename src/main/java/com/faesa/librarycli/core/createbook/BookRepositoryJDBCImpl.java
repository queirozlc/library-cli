package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.core.createauthor.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookRepositoryJDBCImpl implements BookRepository {

    private final Connection connection;
    private final AuthorRepository authorRepository;

    @Override
    public Book save(Book entity) {
        try (var statement = connection.prepareStatement("INSERT INTO book (title, isbn, publication_date, pages, author_id) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
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
        String sql = "SELECT * FROM book WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildBook(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM book WHERE id = ?";
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
        String sql = "SELECT * FROM book";
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var book = buildBook(resultSet);
                book.assignId(resultSet.getLong("id"));
                books.add(book);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM book WHERE id = ?";
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

    private Book buildBook(ResultSet resultSet) throws SQLException {

        var author = authorRepository
                .findById(resultSet.getLong("author_id"))
                .orElseThrow(() -> new RuntimeException("Author not found"));

        var title = resultSet.getString("title");

        var isbn = resultSet.getString("isbn");

        var publicationDate = resultSet.getDate("publication_date").toLocalDate();

        var pages = resultSet.getInt("pages");

        return new Book(title, isbn, publicationDate, pages, author);
    }


    @Override
    public Optional<Book> findByIsbn(String bookIsbn) {
        String sql = "SELECT * FROM book WHERE isbn = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, bookIsbn);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var book = buildBook(resultSet);
                book.assignId(resultSet.getLong("id"));
                return Optional.of(book);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
