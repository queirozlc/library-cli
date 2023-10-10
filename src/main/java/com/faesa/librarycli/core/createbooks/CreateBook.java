package com.faesa.librarycli.core.createbooks;

import jakarta.validation.constraints.NotBlank;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CreateBook {


    @ShellMethod(value = "Creates a new book in the library", key = "create-book", interactionMode = InteractionMode.INTERACTIVE)
    public String createBook(
            @ShellOption @NotBlank String title
    ) {
        System.out.println(title);

        return "Book created";
    }
}
