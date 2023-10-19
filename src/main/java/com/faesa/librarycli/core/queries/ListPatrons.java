package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.registerpatron.PatronRepository;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

import java.util.LinkedHashMap;
import java.util.List;

@ShellComponent
@ShellCommandGroup("Query Commands")
@RequiredArgsConstructor
public class ListPatrons {

    private final ShellHelper shellHelper;
    private final PatronRepository patronRepository;

    @ShellMethod(value = "List all patrons", key = "list-patrons")
    public void listPatrons() {
        List<PatronResponse> patrons = patronRepository.findAll().stream()
                .map(PatronResponse::from)
                .toList();

        var tableHeaders = new LinkedHashMap<String, Object>();
        tableHeaders.put("name", "Patron's Name");
        tableHeaders.put("type", "Patron's Type");
        var model = new BeanListTableModel<>(patrons, tableHeaders);
        var tableBuilder = new TableBuilder(model);
        var table = tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
        shellHelper.print(table.render(100));
    }
}
