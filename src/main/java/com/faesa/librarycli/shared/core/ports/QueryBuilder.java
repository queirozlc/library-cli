package com.faesa.librarycli.shared.core.ports;

import org.springframework.stereotype.Component;

@Component
public interface QueryBuilder {
    String build(Class<?> domainClass);

    // strategy method that going to define the behavior of the query builder based on the type of the argument
    boolean supports(QueryType type);

    String build(Object... args);
}
