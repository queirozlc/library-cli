package com.faesa.librarycli.core.registerpatron;

import lombok.Getter;

public class Patron {
    private String name;
    private PatronType type;

    @Getter
    private Long id;

    public Patron(String name, PatronType type) {
        this.name = name;
        this.type = type;
    }
}
