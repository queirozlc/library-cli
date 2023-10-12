package com.faesa.librarycli.shared.infra.database;

import java.sql.PreparedStatement;

public interface DomainValuesExtractor {
    void extract(PreparedStatement statement);
}
