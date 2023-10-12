package com.faesa.librarycli.core.createbook;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class CreateBook {


    @ShellMethod(value = "Creates a new book in the library", key = "create-book")
    public String createBook() {
        return "Book created successfully";
    }
}
