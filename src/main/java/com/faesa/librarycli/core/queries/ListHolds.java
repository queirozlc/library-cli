package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.placinghold.HoldRepository;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.BeanListTableModel;

import java.util.LinkedHashMap;
import java.util.List;

@ShellComponent
@ShellCommandGroup("Query Commands")
@RequiredArgsConstructor
public class ListHolds {

    private final HoldRepository holdRepository;
    private final ShellHelper shellHelper;

    @ShellMethod(value = "List all holds", key = "list-holds")
    public void listHolds() {
        List<HoldListResponse> holds = holdRepository.findAll()
                .stream()
                .map(HoldListResponse::fromHold)
                .toList();

        var tableHeaders = new LinkedHashMap<String, Object>();
        tableHeaders.put("holdId", "Hold ID");
        tableHeaders.put("bookTitle", "Book Title");
        tableHeaders.put("datePlaced", "Date Placed");
        tableHeaders.put("expirationDays", "Expiration Days");
        tableHeaders.put("fee", "Hold Fee");
        tableHeaders.put("authorName", "Author Name");
        tableHeaders.put("patronName", "Patron Name");
        tableHeaders.put("patronType", "Patron Type");
        var model = new BeanListTableModel<>(holds, tableHeaders);
        var tableBuilder = new org.springframework.shell.table.TableBuilder(model);
        var table = tableBuilder.addFullBorder(org.springframework.shell.table.BorderStyle.fancy_light).build();
        shellHelper.print(table.render(100));
    }
}
