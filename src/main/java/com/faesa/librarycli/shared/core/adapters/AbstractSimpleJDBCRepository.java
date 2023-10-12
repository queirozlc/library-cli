package com.faesa.librarycli.shared.core.adapters;

import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import com.faesa.librarycli.shared.core.ports.QueryType;
import com.faesa.librarycli.shared.infra.database.BaseRepository;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * @param <T>  Entity type
 * @param <ID> Entity ID type
 * @author lucasqueiroz
 *
 * <h2>
 * AbstractSimpleJDBCRepository<T, ID>
 * </h2>
 *
 * <h3>
 * Abstract class that implements the BaseRepository interface and provides a connection to the database.
 * </h3>
 *
 * <p>
 * This class is used to provide an implementation for the base methods of the BaseRepository interface.
 * </p>
 *
 * <p>
 * It going to use the connection provided by the constructor to execute the queries.
 * </p>
 *
 * <p>
 * To deal with unknown types dynamically, the implementations will use the {@link java.lang.reflect} Reflection API which will allow us to handle this types at runtime.
 * </p>
 * @see BaseRepository
 */
@RequiredArgsConstructor
@Component
public abstract class AbstractSimpleJDBCRepository<T extends DomainValuesExtractor, ID> implements BaseRepository<T, ID> {

    private final Connection connection;
    private final Set<QueryBuilder> queryBuilders;

    @Override
    public T save(T entity) {
        var query = getQuery(QueryType.INSERT, entity.getClass());
        // creates a statement object with the option to generate auto generated keys
        // this will allow us to get the id of the inserted entity
        try (var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // set the values of the statement
            entity.extract(statement);

            // execute the statement
            statement.executeUpdate();

            // get the generated keys
            var generatedKeys = statement.getGeneratedKeys();

            // if there is a generated key, set it to the entity
            if (generatedKeys.next()) {
                entity.assignId(generatedKeys.getLong(1));
                return entity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.empty();
    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void deleteById(ID id) {

    }

    @Override
    public Collection<T> findAll() {
        return null;
    }

    @Override
    public boolean existsById(ID id) {
        return false;
    }

    private String getQuery(QueryType type, Class<?> domainClass) {
        return queryBuilders.stream()
                .filter(queryBuilder -> queryBuilder.supports(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No query builder found for type " + type))
                .build(domainClass);
    }

    private String getQueryWithArgs(QueryType type, Class<?> domainClass, Object... args) {
        return queryBuilders.stream()
                .filter(queryBuilder -> queryBuilder.supports(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No query builder found for type " + type))
                .build(domainClass, args);
    }
}
