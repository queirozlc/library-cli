package com.faesa.librarycli.shared.core.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    private final Connection connection;
    private Class<?> domainClass;

    private String fieldName;

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.domainClass = constraintAnnotation.domainClass();
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        var sql = String.format("SELECT 1 FROM %s WHERE %s = ?", domainClass.getSimpleName(), fieldName);
        try (var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            var resultSet = statement.executeQuery();
            return !resultSet.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
