package com.faesa.librarycli.core.createauthor;

import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Author implements DomainValuesExtractor {

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
    public void extract(PreparedStatement statement) {
        try {
            statement.setString(1, name);
            statement.setString(2, nationality);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void assignId(Long id) {
        Assert.state(this.id == null, "Id already assigned");
        this.id = id;
    }
}
