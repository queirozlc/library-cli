package com.faesa.librarycli.core.checkoutbook;

import com.faesa.librarycli.core.newinstance.InstanceRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Optional;

@ShellComponent
@ShellCommandGroup("Patron Commands")
@RequiredArgsConstructor
public class CheckoutBook {

    private final PatronRepository patronRepository;
    private final LoanRepository loanRepository;
    private final InstanceRepository instanceRepository;

    //    @UniqueValue(domainClass = Loan.class, fieldName = "hold_id", message = "You cannot have two checkouts for the same hold")
    @ShellMethod(value = "Lends a book for some patron", key = "checkout-book")
    public String perform(
            @ShellOption(value = {"-p", "--patron"}, help = "Patron's id")
            @NotNull @Positive Long patronId,

            @ShellOption(value = {"-b", "--hold"}, help = "Hold's id")
            @NotNull @Positive Long holdId,
            @ShellOption(value = {"-t", "--time"},
                    help = "Borrowing time in days", defaultValue = ShellOption.NULL)
            @Range(min = 1, max = 60) Integer time
    ) {
        int maxCheckoutTime = Optional.ofNullable(time).orElse(60);
        return patronRepository.findById(patronId).map(patron -> {
            Loan checkout = patron.createCheckout(holdId, maxCheckoutTime);
            loanRepository.save(checkout);
            instanceRepository.save(checkout.currentInstance());
            return "Book successfully checked out";
        }).orElse("Patron not found");
    }
}
