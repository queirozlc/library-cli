package com.faesa.librarycli.core.createbooks;

import java.time.LocalDate;

public record CreateBookRequest(
        String title,
        Long authorId,
        String isbn,
        LocalDate publicationDate,
        Long pages
) {
}
