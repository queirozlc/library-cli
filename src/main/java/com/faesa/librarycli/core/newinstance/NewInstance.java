package com.faesa.librarycli.core.newinstance;

import com.faesa.librarycli.core.createbook.BookRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@ShellComponent
@ShellCommandGroup("Book Commands")
@RequiredArgsConstructor
public class NewInstance {

    private final InstanceRepository instanceRepository;
    private final BookRepository bookRepository;

    @ShellMethod(key = "new-instance", value = "Register a new instance of a book on library")
    public String handle(
            @ShellOption(help = "The book's isbn", value = {"-i", "--isbn"})
            @NotBlank
            @ISBN(type = ISBN.Type.ISBN_13) String bookIsbn,
            @ShellOption(
                    help = "The instance's type, it must be either FREE or RESTRICTED",
                    valueProvider = InstanceTypeValueProvider.class,
                    value = {"-t", "--type"}
            ) @NotBlank String type,
            @ShellOption(
                    help = "The instance's status, it must be either AVAILABLE or UNAVAILABLE",
                    valueProvider = InstanceStatusValueProvider.class,
                    value = {"-s", "--status"},
                    defaultValue = "AVAILABLE"
            ) @NotBlank String status
    ) {
        var instanceStatus = InstanceStatus.valueOf(status.toUpperCase());
        var instanceType = InstanceType.valueOf(type.toUpperCase());
        return bookRepository.findByIsbn(bookIsbn).map(book -> {
            var instance = new Instance(instanceStatus, instanceType, book);
            instanceRepository.save(instance);
            return "Instance created!";
        }).orElse("Book not found!");
    }
}

@Component
class InstanceTypeValueProvider implements ValueProvider {
    @Override
    public List<CompletionProposal> complete(CompletionContext completionContext) {
        return Stream.of(InstanceType.values())
                .map(Enum::name)
                .map(CompletionProposal::new)
                .toList();
    }
}

@Component
class InstanceStatusValueProvider implements ValueProvider {
    @Override
    public List<CompletionProposal> complete(CompletionContext completionContext) {
        return Stream.of(InstanceStatus.values())
                .map(Enum::name)
                .map(CompletionProposal::new)
                .toList();
    }
}