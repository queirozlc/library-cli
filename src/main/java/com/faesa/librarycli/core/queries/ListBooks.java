package com.faesa.librarycli.core.queries;

import com.faesa.librarycli.core.createbook.BookRepository;
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
public class ListBooks {

    private final ShellHelper shellHelper;
    private final BookRepository bookRepository;

    @ShellMethod(value = "List all books", key = "list-books")
    public void execute() {
        List<BookResponse> bookResponses = bookRepository.findAll().stream().map(BookResponse::fromBook).toList();
        var tableHeaders = new LinkedHashMap<String, Object>();
        tableHeaders.put("id", "BOOK ID");
        tableHeaders.put("title", "TITLE");
        tableHeaders.put("authorName", "AUTHOR NAME");
        tableHeaders.put("isbn", "ISBN");
        tableHeaders.put("pages", "PAGES");
        tableHeaders.put("publicationDate", "PUBLICATION DATE");
        var tableModel = new BeanListTableModel<>(bookResponses, tableHeaders);
        var tableBuilder = new TableBuilder(tableModel);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        shellHelper.print(tableBuilder.build().render(100));
    }
}
