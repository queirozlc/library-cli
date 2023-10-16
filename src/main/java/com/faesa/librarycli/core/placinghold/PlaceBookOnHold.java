package com.faesa.librarycli.core.placinghold;

import com.faesa.librarycli.core.createbook.BookRepository;
import com.faesa.librarycli.core.newinstance.InstanceRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup("Patron Commands")
@RequiredArgsConstructor
public class PlaceBookOnHold {

    private final PatronRepository patronRepository;
    private final BookRepository bookRepository;
    private final InstanceRepository instanceRepository;
    private final HoldRepository holdRepository;

    @ShellMethod(key = "hold-book", value = "Place a book on hold for a patron")
    public String perform(
            @ShellOption(
                    help = "The patron's ID",
                    value = {"-p", "--patron-id"}
            ) @NotNull @Positive final Long patronId,
            @ShellOption(
                    help = "The book's ISBN",
                    value = {"-i", "--isbn"}
            ) @NotBlank @ISBN(type = ISBN.Type.ISBN_10) String isbn,
            @ShellOption(
                    help = "The number of days the hold will last",
                    value = {"-d", "--days-to-expire"},
                    defaultValue = "0"
            ) @PositiveOrZero final Integer daysToExpire
    ) {
        var patron = patronRepository.findById(patronId);

        return patron.map(patronFound -> bookRepository.findByIsbn(isbn).map(book -> {
            if (book.canBePlacedOnHold(patronFound)) {
                Hold hold = book.placeOnHold(patronFound, instanceRepository::save);
                if (daysToExpire > 0) {
                    hold.expireIn(daysToExpire);
                }
                holdRepository.save(hold);
                return "Hold placed successfully. Hold ID: " + hold.getId();
            }

            return "Book does not support hold";
        }).orElse("Book not found")).orElse("Patron not found");
    }
}
