package com.faesa.librarycli.core.patronprofile;

import com.faesa.librarycli.core.placinghold.HoldRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import java.util.stream.Stream;

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
                    .toArray(HoldResponse[]::new);
            buildTableData(holdResponse);
        });
    }

    private void buildTableData(HoldResponse[] holdResponse) {
        String[] holdArray = Stream.of(holdResponse)
                .map(hold -> new String[]{
                        hold.holdId().toString(),
                        hold.instanceId().toString(),
                        hold.title(),
                        hold.authorName(),
                        hold.datePlaced(),
                        hold.daysToExpire().toString(),
                        hold.holdFee() * 100 + "%"
                })
                .flatMap(Stream::of)
                .toArray(String[]::new);

        Object[][] tableData = new String[][]{
                TABLE_HEADERS,
                holdArray
        };

        TableModel model = new ArrayTableModel(tableData);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        helper.print(tableBuilder.build().render(100));
    }
}
