package com.faesa.librarycli.core.returnslend;

import com.faesa.librarycli.core.createbook.BookRepository;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.newinstance.InstanceRepository;
import com.faesa.librarycli.core.placinghold.HoldRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup("Patron Commands")
@RequiredArgsConstructor
public class ReturnsLend {

    private final PatronRepository patronRepository;
    private final InstanceRepository instanceRepository;
    private final HoldRepository holdRepository;
    private final BookRepository bookRepository;

    @ShellMethod(value = "Return a borrowed book", key = "return-book")
    public String perform(
            @ShellOption(
                    value = {"-p", "--patron"},
                    help = "Patron's ID"
            )
            @NotNull @Positive
            Long patronId,
            @ShellOption(
                    value = {"-b", "--book"},
                    help = "Book's ID"
            )
            @Positive @NotNull
            Long bookId
    ) {
        return patronRepository.findById(patronId).map(patron -> bookRepository.findById(bookId).map(book -> {
            if (book.hasInstanceBorrowedBy(patron)) {
                Long holdId = patron.getHoldIdFor(book);
                Instance instance = patron.getHoldInstanceFor(book);
                patron.returnBook(book);
                holdRepository.deleteById(holdId);
                instance.instanceReturned();
                instanceRepository.save(instance);
                patronRepository.save(patron);
                return "Book returned";
            }
            return "Book not borrowed by patron";
        }).orElse("Book not found")).orElse("Patron not found");
    }
}
