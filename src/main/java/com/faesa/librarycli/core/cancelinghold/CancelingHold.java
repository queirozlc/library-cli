package com.faesa.librarycli.core.cancelinghold;

import com.faesa.librarycli.core.createbook.BookRepository;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.newinstance.InstanceRepository;
import com.faesa.librarycli.core.placinghold.HoldRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import com.faesa.librarycli.shared.infra.shell.InputReader;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@ShellCommandGroup("Patron Commands")
@RequiredArgsConstructor
public class CancelingHold {

    private final PatronRepository patronRepository;
    private final HoldRepository holdRepository;
    private final BookRepository bookRepository;
    private final InstanceRepository instanceRepository;
    private final InputReader inputReader;

    @ShellMethod(value = "Cancel a book hold", key = "cancel-hold")
    public String perform(
            @ShellOption(value = {"-p", "--patron"}, help = "Patron ID")
            @Positive @NotNull Long patronId,
            @ShellOption(value = {"-b", "--book"}, help = "Book ID")
            @Positive @NotNull Long bookId
    ) {
        String confirmation = "";
        do {
            confirmation = inputReader.promptWithOptions(
                    "Are you sure you want to cancel this hold?",
                    null,
                    List.of("Yes", "No", "y", "n", "yes", "no")
            );
        } while (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("y") && !confirmation.equalsIgnoreCase("no") && !confirmation.equalsIgnoreCase("n"));


        return patronRepository.findById(patronId).map(patron -> bookRepository.findById(bookId).map(book -> {
            if (book.hasHoldFrom(patron)) {
                var holdToCancel = patron.getHoldIdFor(book);
                Instance instanceHeld = patron.getHoldInstanceFor(book);
                patron.cancelHold(book);
                patronRepository.save(patron);
                instanceRepository.save(instanceHeld);
                holdRepository.deleteById(holdToCancel);
                return "Hold canceled";
            }
            return "This patron doesn't have a hold for this book";
        }).orElse("Book not found")).orElse("Patron not found");
    }

}
