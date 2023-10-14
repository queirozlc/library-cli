package com.faesa.librarycli.core.registerpatron;

import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patron implements DomainValuesExtractor<Long> {
    private String name;
    private PatronType type;

    @Getter
    private Long id;

    public Patron(String name, PatronType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void setStatementValues(PreparedStatement statement) {
        try {
            statement.setString(1, name);
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
            this.name = resultSet.getString("name");
            this.type = PatronType.supports(resultSet.getString("type"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean canBorrowRestrictedInstances() {
        return type == PatronType.RESEARCHER || type == PatronType.STUDENT;
    }

    public boolean hasId() {
        return id != null;
    }

    public Integer getHoldDuration() {
        return type.availableHoldDuration();
    }

    public BigDecimal feeForPlacingHold() {
        return type.currentHoldFee();
    }
}
