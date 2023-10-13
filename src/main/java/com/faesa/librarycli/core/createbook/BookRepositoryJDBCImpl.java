package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.shared.core.adapters.AbstractSimpleJDBCRepository;
import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Set;

@Component
public class BookRepositoryJDBCImpl extends AbstractSimpleJDBCRepository<Book, Long> implements BookRepository {
    public BookRepositoryJDBCImpl(Connection connection, Set<QueryBuilder> queryBuilders) {
        super(connection, queryBuilders);
    }
}
