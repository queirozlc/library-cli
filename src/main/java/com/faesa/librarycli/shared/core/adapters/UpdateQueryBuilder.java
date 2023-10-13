package com.faesa.librarycli.shared.core.adapters;

import com.faesa.librarycli.shared.core.ports.CaseConverter;
import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import com.faesa.librarycli.shared.core.ports.QueryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateQueryBuilder implements QueryBuilder {

    private final CaseConverter caseConverter;

    @Override
    public String build(Class<?> domainClass) {
        var tableName = caseConverter.toSnakeCase(domainClass.getSimpleName());
        var fields = domainClass.getDeclaredFields();
        var sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (var field : fields) {
            var fieldName = caseConverter.toSnakeCase(field.getName());
            if (fieldName.equals("id")) {
                continue;
            }
            sql.append(fieldName).append(" = ?, ");
        }
        sql.delete(sql.length() - 2, sql.length());
        sql.append(" WHERE id = ?");
        return sql.toString();
    }

    @Override
    public boolean supports(QueryType type) {
        return type == QueryType.UPDATE;
    }
}
