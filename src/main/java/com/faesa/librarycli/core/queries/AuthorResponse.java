package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.createauthor.Author;

public record AuthorResponse(
        String name,
        String nationality
) {

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.getName(), author.getNationality());
    }
}
