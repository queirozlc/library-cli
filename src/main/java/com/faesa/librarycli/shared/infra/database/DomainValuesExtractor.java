package com.faesa.librarycli.shared.infra.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface DomainValuesExtractor<ID> {
    void extract(PreparedStatement statement);

    void assignId(Object id);

    void fromResultSet(ResultSet resultSet);

    ID getId();
}
