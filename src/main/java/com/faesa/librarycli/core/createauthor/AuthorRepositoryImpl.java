package com.faesa.librarycli.core.createauthor;

import com.faesa.librarycli.shared.core.adapters.AbstractSimpleJDBCRepository;
import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Set;


@Component
public class AuthorRepositoryImpl extends AbstractSimpleJDBCRepository<Author, Long> implements AuthorRepository {
    public AuthorRepositoryImpl(Connection connection, Set<QueryBuilder> queryBuilders) {
        super(connection, queryBuilders);
    }
}
