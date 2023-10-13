package com.faesa.librarycli.core.createauthor;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class CreateAuthor {

    private final AuthorRepository authorRepository;

    @ShellMethod(value = "Creates a new author", key = "create-author")
    public String execute(
            @ShellOption(help = "Author's name") @NotBlank String name,
            @ShellOption(help = "Author's nationality") @NotBlank String nationality
    ) {
        var author = new Author(name, nationality);
        authorRepository.save(author);
        return "Author created successfully";
    }
}
