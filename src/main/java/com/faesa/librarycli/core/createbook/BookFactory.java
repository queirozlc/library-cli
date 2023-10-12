package com.faesa.librarycli.core.createbooks;

import com.faesa.librarycli.shared.infra.shell.InputReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookFactory {

    private final InputReader reader;

    public BookFactory(InputReader reader) {
        this.reader = reader;
    }

    public CreateBookRequest createBookRequest() {
        String title = reader.promptWhileEmpty("Title");
        String author = reader.promptWhileEmpty("Author");
        String isbn = reader.promptWhileEmpty("ISBN");
        String publicationDate = reader.promptWhileEmpty("Publication date");
        String pages = reader.promptWhileEmpty("Pages");

        if (title == null || author == null || isbn == null || publicationDate == null || pages == null) {
            return null;
        }

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || publicationDate.isEmpty() || pages.isEmpty()) {
            return null;
        }

        Long authorId = Long.parseLong(author);
        Long pagesLong = Long.parseLong(pages);
        LocalDate publicationDateLocalDate = LocalDate.parse(publicationDate);

        return new CreateBookRequest(title, authorId, isbn, publicationDateLocalDate, pagesLong);
    }
}
