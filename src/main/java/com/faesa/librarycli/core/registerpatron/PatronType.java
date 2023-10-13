package com.faesa.librarycli.core.registerpatron;

public enum PatronType {
    STUDENT,
    RESEARCHER,
    REGULAR;

    public static PatronType supports(String type) {
        return PatronType.valueOf(type.toUpperCase());
    }
}
