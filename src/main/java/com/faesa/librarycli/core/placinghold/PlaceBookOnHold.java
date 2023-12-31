package com.faesa.librarycli.core.placinghold;

import com.faesa.librarycli.core.createbook.BookRepository;
import com.faesa.librarycli.core.newinstance.InstanceRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import com.faesa.librarycli.shared.infra.shell.DefaultOutput;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Optional;

@ShellComponent
@ShellCommandGroup("Patron Commands")
@RequiredArgsConstructor
public class PlaceBookOnHold {

    private final PatronRepository patronRepository;
    private final BookRepository bookRepository;
    private final InstanceRepository instanceRepository;
    private final HoldRepository holdRepository;
    private final ShellHelper shellHelper;
    private final DefaultOutput output;

    @ShellMethod(key = "place-hold", value = "Place a book on hold for a patron")
    public String perform(
            @ShellOption(
                    help = "The patron's ID",
                    value = {"-p", "--patron-id"}
            ) @NotNull @Positive final Long patronId,
            @ShellOption(
                    help = "The book's ISBN",
                    value = {"-i", "--isbn"}
            ) @NotBlank @ISBN(type = ISBN.Type.ISBN_13) String isbn,
            @ShellOption(
                    help = "The number of days the hold will last",
                    value = {"-d", "--days-to-expire"},
                    defaultValue = ShellOption.NULL
            ) @Positive final Integer daysToExpire
    ) {
        var patron = patronRepository.findById(patronId);

        return patron.map(patronFound -> bookRepository.findByIsbn(isbn).map(book -> {
                    int daysToExpireOrDefault = Optional.ofNullable(daysToExpire).orElse(patronFound.getHoldDuration());
                    if (book.canBePlacedOnHold(patronFound, daysToExpireOrDefault)) {
                        Hold hold = book.placeOnHold(patronFound, daysToExpireOrDefault, instanceRepository::save);
                        holdRepository.save(hold);
                        shellHelper.printSuccess("\nHold placed successfully\n");
                        return output.build();
                    }

                    shellHelper.printError("Book does not support hold or patron has reached the maximum number of holds");
                    return output.build();
                })
                .orElseGet(() -> {
                    shellHelper.printError("\nBook not found\n");
                    return output.build();
                })).orElseGet(() -> {
            shellHelper.printError("\nPatron not found\n");
            return output.build();
        });
    }
}
