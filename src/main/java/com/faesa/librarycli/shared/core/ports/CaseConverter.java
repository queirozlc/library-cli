package com.faesa.librarycli.shared.core.ports;

import org.springframework.stereotype.Component;

@Component
public class CaseConverter {

    public String toSnakeCase(String value) {
        var builder = new StringBuilder();
        for (var i = 0; i < value.length(); i++) {
            var c = value.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append("_").append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public String toCamelCase(String value) {
        var builder = new StringBuilder();
        var upper = false;
        for (var i = 0; i < value.length(); i++) {
            var c = value.charAt(i);
            if (c == '_') {
                upper = true;
            } else {
                if (upper) {
                    builder.append(Character.toUpperCase(c));
                    upper = false;
                } else {
                    builder.append(c);
                }
            }
        }
        return builder.toString();
    }
}
