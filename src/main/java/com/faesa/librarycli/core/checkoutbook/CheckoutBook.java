package com.faesa.librarycli.core.checkoutbook;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup("Patron Commands")
@RequiredArgsConstructor
public class CheckoutBook {


    @ShellMethod(value = "Lends a book for some patron", key = "checkout-book")
    public String perform(
            @ShellOption(value = {"-p", "--patron"}, help = "Patron's id")
            @NotNull @Positive Long patronId,

            @ShellOption(value = {"-b", "--hold"}, help = "Hold's id")
            @NotNull @Positive Long holdId,

            @ShellOption(value = {"-t", "--time"}, help = "Borrowing time in days")
            @NotNull @Positive @Range(min = 1, max = 60) Integer time
    ) {
        return "";
    }
}
