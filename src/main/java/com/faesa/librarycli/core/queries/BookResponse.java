package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.createbook.Book;

import java.time.format.DateTimeFormatter;

public record BookResponse(
        String id,
        String title,
        String authorName,
        String isbn,
        String pages,
        String publicationDate
) {

    public static BookResponse fromBook(Book book) {
        return new BookResponse(
                book.getId().toString(),
                book.getTitle(),
                book.getAuthorName(),
                book.getIsbn(),
                book.getPages().toString(),
                book.formatPublicationDate(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }
}
