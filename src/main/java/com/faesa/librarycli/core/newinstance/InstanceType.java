package com.faesa.librarycli.core.newinstance;

public enum InstanceType {

    FREE,
    RESTRICTED;

    public static InstanceType supports(String value) {
        return InstanceType.valueOf(value.toUpperCase());
    }
}
