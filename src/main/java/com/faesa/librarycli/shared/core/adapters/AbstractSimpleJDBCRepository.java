package com.faesa.librarycli.shared.core.adapters;

import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import com.faesa.librarycli.shared.core.ports.QueryType;
import com.faesa.librarycli.shared.infra.database.BaseRepository;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
public abstract class AbstractSimpleJDBCRepository<T extends DomainValuesExtractor<ID>, ID> implements BaseRepository<T, ID> {

    protected final Connection connection;
    private final Set<QueryBuilder> queryBuilders;

    @Override
    public T save(T entity) {
        // if the entity has an id, it means that it already exists in the database, then will be updated
        if (entity.getId() != null) {
            return update(entity);
        }

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
                // cannot convert integer object from result set to long, has to be another way
                entity.assignId(generatedKeys.getObject(1));
                return entity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private T update(T entity) {
        var query = getQuery(QueryType.UPDATE, entity.getClass());
        try (var statement = connection.prepareStatement(query)) {
            entity.extract(statement);
            statement.setObject(entity.getClass().getDeclaredFields().length + 1, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        Class<T> domainClass = getDomainClass();
        String query = getQuery(QueryType.SELECT_BY_ID, domainClass);
        try (var statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                domainClass.getDeclaredConstructor().setAccessible(true);
                T entity = domainClass.getDeclaredConstructor().newInstance();
                entity.fromResultSet(resultSet);
                return Optional.of(entity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    @Override
    public void deleteById(ID id) {
        var query = getQuery(QueryType.DELETE, getDomainClass());
        try (var statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<T> findAll() {
        var entities = new ArrayList<>(Collections.<T>emptyList());
        var query = getQuery(QueryType.SELECT, getDomainClass());
        try (var statement = connection.prepareStatement(query)) {
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                getDomainClass().getDeclaredConstructor().setAccessible(true);
                var entity = getDomainClass().getDeclaredConstructor().newInstance();
                entity.fromResultSet(resultSet);
                entities.add(entity);
            }

        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return entities;
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    private String getQuery(QueryType type, Class<?> domainClass) {
        return queryBuilders.stream()
                .filter(queryBuilder -> queryBuilder.supports(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No query builder found for type " + type))
                .build(domainClass);
    }

    private Class<T> getDomainClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Class<?> domainClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        return (Class<T>) domainClass;
    }
}
