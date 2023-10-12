package com.faesa.librarycli.shared.core.adapters;

import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import com.faesa.librarycli.shared.core.ports.QueryType;
import org.springframework.stereotype.Component;

@Component
public class SelectByIdQueryBuilder implements QueryBuilder {
    @Override
    public String build(Class<?> domainClass) {
        return "SELECT * FROM " + domainClass.getSimpleName().toLowerCase() +
                " WHERE id = ?";
    }

    @Override
    public boolean supports(QueryType type) {
        return type.equals(QueryType.SELECT_BY_ID);
    }
}
