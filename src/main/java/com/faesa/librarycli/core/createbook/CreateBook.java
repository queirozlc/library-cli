package com.faesa.librarycli.core.createbooks;

import com.faesa.librarycli.core.createauthor.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class CreateBook {

    private final BookFactory bookFactory;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @ShellMethod(value = "Creates a new book in the library", key = "create-book")
    public String createBook() {
        CreateBookRequest request = bookFactory.createBookRequest();

        if (request == null) {
            return "Invalid input";
        }

        var book = request.toDomain(authorRepository::findById);
        bookRepository.save(book);

        return "Book created successfully";
    }
}
