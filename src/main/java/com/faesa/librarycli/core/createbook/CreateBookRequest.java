package com.faesa.librarycli.core.createbooks;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateBookRequest(
        @NotBlank
        String title,
        @NotNull
        @Positive
        Long authorId,

        @ISBN
        @NotNull
        String isbn,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull
        @PastOrPresent
        LocalDate publicationDate,

        @Positive
        @NotNull
        Long pages
) {
}
