package com.faesa.librarycli.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultOutput {

    
    public String build() {
        return "Hello World";
    }
}
