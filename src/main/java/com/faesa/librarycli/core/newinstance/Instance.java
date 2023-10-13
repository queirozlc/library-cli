package com.faesa.librarycli.core.newinstance;

import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Instance implements DomainValuesExtractor<Long> {

    private Long id;

    private InstanceStatus status;

    private InstanceType type;

    private Book book;

    @Override
    public void setStatementValues(PreparedStatement statement) {
        try {
            statement.setString(1, status.name());
            statement.setString(2, type.name());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void assignId(Long id) {
        Assert.state(this.id == null, "Id already assigned");
        this.id = id;
    }

    @Override
    public void fromResultSet(ResultSet resultSet) {
        try {
            this.id = resultSet.getLong("id");
            this.status = InstanceStatus.valueOf(resultSet.getString("status"));
            this.type = InstanceType.supports(resultSet.getString("type"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long getId() {
        return null;
    }
}
