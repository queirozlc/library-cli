package com.faesa.librarycli.shared.infra.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface DomainValuesExtractor {
    void extract(PreparedStatement statement);

    void assignId(Long id);

    void fromResultSet(ResultSet resultSet);
}
