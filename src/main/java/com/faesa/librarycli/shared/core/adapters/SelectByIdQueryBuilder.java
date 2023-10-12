package com.faesa.librarycli.shared.core.adapters;

import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import com.faesa.librarycli.shared.core.ports.QueryType;
import org.springframework.stereotype.Component;

@Component
public class SelectQueryBuilder implements QueryBuilder {
    @Override
    public String build(Class<?> domainClass) {
        String builder = "SELECT * FROM " + domainClass.getSimpleName().toLowerCase() +
                "s";
        return builder;
    }

    @Override
    public boolean supports(QueryType type) {
        return type.equals(QueryType.SELECT);
    }
}
