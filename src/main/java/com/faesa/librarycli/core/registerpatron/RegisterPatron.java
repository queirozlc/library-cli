package com.faesa.librarycli.core.registerpatron;

import com.faesa.librarycli.shared.infra.shell.DefaultOutput;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@ShellComponent
@ShellCommandGroup("Patron Commands")
@RequiredArgsConstructor
public class RegisterPatron {

    private final PatronRepository patronRepository;
    private final ShellHelper shellHelper;
    private final DefaultOutput output;

    @ShellMethod(key = "create-patron", value = "Register a new patron on library", interactionMode = InteractionMode.ALL)
    public String handle(
            @ShellOption @NotBlank String name,
            @ShellOption(value = {
                    "-t", "--type"
            }, help = "Patron type, must be one of: STUDENT, RESEARCHER, REGULAR",
                    valueProvider = PatronTypeValueProvider.class,
                    defaultValue = "REGULAR"
            ) @NotBlank String type
    ) {
        var patronType = PatronType.supports(type);
        Assert.notNull(patronType, "Patron type must not be null, type must be one of: STUDENT, RESEARCHER, REGULAR");
        var patron = new Patron(name, patronType);
        patronRepository.save(patron);
        shellHelper.printSuccess("\nPatron created with id: " + patron.getId() + "\n");
        return output.build();
    }


}

@Component
class PatronTypeValueProvider implements ValueProvider {
    @Override
    public List<CompletionProposal> complete(CompletionContext completionContext) {
        return Arrays.stream(PatronType.values())
                .map(PatronType::name)
                .map(CompletionProposal::new)
                .toList();
    }
}
