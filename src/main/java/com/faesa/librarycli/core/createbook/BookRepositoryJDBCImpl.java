package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.shared.core.adapters.AbstractSimpleJDBCRepository;
import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Set;

@Component
public class BookRepositoryJDBCImpl extends AbstractSimpleJDBCRepository<Book, Long> implements BookRepository {

    public BookRepositoryJDBCImpl(Connection connection, Set<QueryBuilder> queryBuilders) {
        super(connection, queryBuilders);
    }

    @Override
    public Book save(Book entity) {
        try (var statement = connection.prepareStatement("INSERT INTO book (title, isbn, publication_date, pages, author_id) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            entity.extract(statement);
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
}
