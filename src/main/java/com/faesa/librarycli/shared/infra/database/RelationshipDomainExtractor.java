package com.faesa.librarycli.shared.infra.database;

import java.sql.ResultSet;

public interface RelationshipDomainExtractor<ID> extends DomainValuesExtractor<ID> {

    void fromResultSetWithRelationships(ResultSet resultSet);
}
