package com.faesa.librarycli.core.createauthor;

import com.faesa.librarycli.shared.infra.shell.DefaultOutput;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup("Book Commands")
@RequiredArgsConstructor
public class CreateAuthor {

    private final AuthorRepository authorRepository;
    private final ShellHelper shellHelper;
    private final DefaultOutput output;

    @ShellMethod(value = "Creates a new author", key = "create-author")
    public String execute(
            @ShellOption(help = "Author's name",
                    value = {"-n", "--name"}
            ) @NotBlank String name,
            @ShellOption(help = "Author's nationality"
                    , value = {"-c", "--country"}
            ) @NotBlank String nationality
    ) {
        var author = new Author(name, nationality);
        authorRepository.save(author);
        shellHelper.printSuccess("\nAuthor created successfully\n");
        return output.build();
    }
}
