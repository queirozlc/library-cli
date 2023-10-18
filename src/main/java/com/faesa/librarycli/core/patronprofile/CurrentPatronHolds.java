package com.faesa.librarycli.core.patronprofile;

import com.faesa.librarycli.core.placinghold.HoldRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import java.util.LinkedHashMap;
import java.util.List;

@ShellComponent
@ShellCommandGroup("Patron Profile Commands")
@RequiredArgsConstructor
public class CurrentPatronHolds {
    private static final String[] TABLE_HEADERS = {"Hold Id", "Instance Id", "Book Title", "Author Name", "Date Placed", "Days to Expire", "Hold Fee"};


    private final HoldRepository holdRepository;
    private final PatronRepository patronRepository;
    private final ShellHelper helper;

    @ShellMethod(value = "Get all current holds by patron", key = "patron-holds")
    public void perform(
            @ShellOption(value = {"-p", "--patron-id"}) Long patronId
    ) {
        patronRepository.findById(patronId).ifPresent(patron -> {
            var holdResponse = holdRepository
                    .findAllPatronHolds(patron).stream()
                    .map(HoldResponse::from)
                    .toList();
            if (holdResponse.isEmpty()) {
                helper.print("No holds found");
                return;
            }
            buildTableData(holdResponse);
        });
    }

    private void buildTableData(List<HoldResponse> holdResponse) {
        var headers = new LinkedHashMap<String, Object>();
        headers.put("holdId", TABLE_HEADERS[0]);
        headers.put("instanceId", TABLE_HEADERS[1]);
        headers.put("title", TABLE_HEADERS[2]);
        headers.put("authorName", TABLE_HEADERS[3]);
        headers.put("datePlaced", TABLE_HEADERS[4]);
        headers.put("daysToExpire", TABLE_HEADERS[5]);
        headers.put("holdFee", TABLE_HEADERS[6]);

        TableModel model = new BeanListTableModel<>(holdResponse, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        helper.print(tableBuilder.build().render(100));
    }
}
