package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.registerpatron.Patron;

public record PatronResponse(
        String name,
        String type
) {

    public static PatronResponse from(Patron patron) {
        return new PatronResponse(patron.getName(), patron.getType());
    }
}
