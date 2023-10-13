package com.faesa.librarycli.core.registerpatron;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class RegisterPatron {

    private final PatronRepository patronRepository;

    @ShellMethod(key = "create-patron", value = "Register a new patron on library")
    public String handle() {
        return "Patron created successfully!";
    }

}
