package com.faesa.librarycli.shared.infra.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface DomainValuesExtractor<ID> {
    void setStatementValues(PreparedStatement statement);

    void assignId(ID id);

    void fromResultSet(ResultSet resultSet);

    ID getId();
}
