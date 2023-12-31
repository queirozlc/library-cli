package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.core.createauthor.AuthorRepository;
import com.faesa.librarycli.shared.core.validators.UniqueValue;
import com.faesa.librarycli.shared.infra.shell.DefaultOutput;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ShellComponent
@ShellCommandGroup("Book Commands")
@RequiredArgsConstructor
public class CreateBook {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ShellHelper shellHelper;
    private final DefaultOutput output;


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
            ) @NotBlank @ISBN(type = ISBN.Type.ISBN_13) @UniqueValue(domainClass = Book.class, fieldName = "isbn", message = "Already exists a book with this ISBN") String isbn,
            @ShellOption(
                    help = "The publication date of the book",
                    value = {"-d", "--publication-date"}
            ) @NotBlank
            // regex pattern to validate date in format dd/MM/yyyy
            @Pattern(regexp = "^([0-2][0-9]|3[0-1])/(0[1-9]|1[0-2])/\\d{4}$", message = "Date must be in format dd/MM/yyyy")
            String publicationDate,
            @ShellOption(
                    help = "The number of pages of the book",
                    value = {"-p", "--pages"}
            ) @NotNull @Positive Integer pages
    ) {
        var possibleAuthor = authorRepository.findById(authorId);
        if (possibleAuthor.isEmpty()) {
            shellHelper.printError("\nAuthor not found\n");
            return output.build();
        }
        var parsedPublicationDate = LocalDate.parse(publicationDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        var author = possibleAuthor.get();
        var book = new Book(title, isbn, parsedPublicationDate, pages, author);
        bookRepository.save(book);
        shellHelper.printSuccess("\nBook created successfully\n");
        return output.build();
    }
}
