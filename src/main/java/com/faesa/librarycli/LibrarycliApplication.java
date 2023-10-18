package com.faesa.librarycli;

import com.faesa.librarycli.shared.infra.shell.DefaultOutput;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class LibrarycliApplication {
    private final DefaultOutput output;

    public static void main(String[] args) {
        SpringApplication.run(LibrarycliApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println(output.build());
    }

}
