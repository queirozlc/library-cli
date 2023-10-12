package com.faesa.librarycli.shared.core.adapters;

import com.faesa.librarycli.shared.core.ports.QueryBuilder;
import com.faesa.librarycli.shared.core.ports.QueryType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class InsertQueryBuilder implements QueryBuilder {

    @Override
    public String build(Class<?> domainClass) {
        StringBuilder builder = new StringBuilder("INSERT INTO ");
        builder.append(domainClass.getSimpleName().toLowerCase());
        builder.append(" (");
        List<Field> fields = new ArrayList<>(Arrays.stream(domainClass.getDeclaredFields()).toList());

        // remove the id field because will be auto generated
        return resolveQueryFields(builder, fields);
    }

    @Override
    public boolean supports(QueryType type) {
        return type.equals(QueryType.INSERT);
    }

    
    private String resolveQueryFields(StringBuilder builder, List<Field> fields) {
        fields.removeIf(field -> field.getName().equalsIgnoreCase("id"));

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            builder.append(field.getName());
            if (i < fields.size() - 1) {
                builder.append(", ");
            }
        }

        builder.append(") VALUES (");

        fields.forEach(field -> {
            builder.append("?");
            if (fields.indexOf(field) < fields.size() - 1) {
                builder.append(", ");
            }
        });

        return builder.append(")").toString();
    }
}
