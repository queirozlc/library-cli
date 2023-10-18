package com.faesa.librarycli.core.patronprofile;

import com.faesa.librarycli.core.registerpatron.PatronRepository;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

import java.util.LinkedHashMap;
import java.util.List;

@ShellComponent
@ShellCommandGroup("Patron Profile Commands")
@RequiredArgsConstructor
public class AmountOfHeldBooksPerAuthor {

    private final PatronRepository patronRepository;
    private final ShellHelper shellHelper;

    @ShellMethod(value = "Get the amount of books held by a patron per author", key = "books-held-per-author")
    public void perform(
            @ShellOption(
                    value = {"-p", "--patron"},
                    help = "Patron's ID"
            ) @Positive @NotNull Long patronId
    ) {
        patronRepository.findById(patronId).ifPresent(patron -> {
            var result = patronRepository.findBooksHeldPerAuthor(patron);
            buildTable(result);
        });
    }

    private void buildTable(List<HeldAmountPerAuthor> heldAmountPerAuthors) {
        var tableHeaders = new LinkedHashMap<String, Object>();
        tableHeaders.put("author", "Author");
        tableHeaders.put("numberOfBooksHeld", "Amount Of Books Held");

        var model = new BeanListTableModel<>(heldAmountPerAuthors, tableHeaders);
        var tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        shellHelper.print(tableBuilder.build().render(80));
    }
}
