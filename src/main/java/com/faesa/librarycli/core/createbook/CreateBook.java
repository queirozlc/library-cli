package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.core.createauthor.AuthorRepository;
import com.faesa.librarycli.shared.core.validators.UniqueValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ShellComponent
@RequiredArgsConstructor
public class CreateBook {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;


    @ShellMethod(value = "Creates a new book in the library", key = "create-book")
    public String createBook(
            @ShellOption(
                    help = "The title of the book",
                    value = {"-t", "--title"}
            ) @NotBlank String title,
            @ShellOption(
                    help = "The id of the author of the book",
                    value = {"-a", "--author-id"}
            ) @NotNull @Positive Long authorId,
            @ShellOption(
                    help = "The ISBN of the book",
                    value = {"-i", "--isbn"}
            ) @NotBlank @ISBN(type = ISBN.Type.ISBN_10) @UniqueValue(domainClass = Book.class, fieldName = "isbn") String isbn,
            @ShellOption(
                    help = "The publication date of the book",
                    value = {"-d", "--publication-date"}
            ) @NotBlank
            @Pattern(regexp = "^(0[1-9]|1[0-2])/(0[1-9]|1[0-9]|2[0-9]|3[0-1])/(19|20)\\d{2}$")
            String publicationDate,
            @ShellOption(
                    help = "The number of pages of the book",
                    value = {"-p", "--pages"}
            ) @NotNull @Positive Integer pages
    ) {
        var possibleAuthor = authorRepository.findById(authorId);
        if (possibleAuthor.isEmpty()) {
            return "Author not found";
        }
        var parsedPublicationDate = LocalDate.parse(publicationDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        var author = possibleAuthor.get();
        var book = new Book(title, isbn, parsedPublicationDate, pages, author);
        bookRepository.save(book);
        return "Book created successfully";
    }
}
