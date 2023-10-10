package com.faesa.librarycli.shared.infra.config;

import com.faesa.librarycli.shared.infra.database.DatabaseProvider;
import com.faesa.librarycli.shared.infra.shell.InputReader;
import com.faesa.librarycli.shared.infra.shell.ShellHelper;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.sql.Connection;

@Configuration
public class SharedAppConfiguration {

    @Bean
    public Connection connection() {
        return DatabaseProvider.getConnection();
    }

    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public InputReader inputReader(
            @Lazy Terminal terminal,
            @Lazy Parser parser,

            @Lazy History history,
            ShellHelper shellHelper
    ) {
        LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder()
                .terminal(terminal)
                .history(history)
                .parser(parser);

        LineReader lineReader = lineReaderBuilder.build();
        lineReader.unsetOpt(LineReader.Option.INSERT_TAB);
        return new InputReader(lineReader, shellHelper);
    }
}
