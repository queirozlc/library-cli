package com.faesa.librarycli.core.createauthor;

import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Author implements DomainValuesExtractor<Long> {

    @Getter
    private Long id;
    private String name;
    private String nationality;

    public Author() {
    }

    public Author(String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
    }

    @Override
    public void setStatementValues(PreparedStatement statement) {
        try {
            statement.setString(1, name);
            statement.setString(2, nationality);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void assignId(Object id) {
        Assert.state(this.id == null, "Id already assigned");
        this.id = Long.parseLong(String.valueOf(id));
    }

    @Override
    public void fromResultSet(ResultSet resultSet) {
        try {
            this.id = resultSet.getLong("id");
            this.name = resultSet.getString("name");
            this.nationality = resultSet.getString("nationality");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
