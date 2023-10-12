package com.faesa.librarycli.shared.core.adapters;

import com.faesa.librarycli.shared.infra.database.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;

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
public abstract class AbstractSimpleJDBCRepository<T, ID> implements BaseRepository<T, ID> {

    private final Connection connection;
}
