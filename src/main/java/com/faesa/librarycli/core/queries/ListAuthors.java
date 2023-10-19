package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.createauthor.AuthorRepository;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;

import java.util.LinkedHashMap;
import java.util.List;

@ShellComponent
@ShellCommandGroup("Query Commands")
@RequiredArgsConstructor
public class ListAuthors {

    private final AuthorRepository authorRepository;
    private final ShellHelper shellHelper;

    @ShellMethod(value = "List all authors", key = "list-authors")
    public void listAuthors() {
        List<AuthorResponse> authors = authorRepository.findAll().stream().map(AuthorResponse::from).toList();
        var tableHeaders = new LinkedHashMap<String, Object>();
        tableHeaders.put("name", "Authorlist's Name");
        tableHeaders.put("nationality", "Author's Nationality");
        var model = new BeanListTableModel<>(authors, tableHeaders);
        var tableBuilder = new TableBuilder(model);
        Table table = tableBuilder.addHeaderBorder(BorderStyle.air)
                .addFullBorder(BorderStyle.fancy_light).build();
        shellHelper.print(table.render(100));
    }
}
