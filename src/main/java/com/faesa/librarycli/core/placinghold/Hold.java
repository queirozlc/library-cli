package com.faesa.librarycli.core.placinghold;

import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Hold implements DomainValuesExtractor<Long> {
    private final Patron patron;
    private final Instance instance;
    private Long id;
    private LocalDate datePlaced;

    private Integer daysToExpire;

    private BigDecimal holdFee;

    public Hold(Patron patron, Instance instance) {
        this.patron = patron;
        this.instance = instance;
        this.datePlaced = LocalDate.now();
        this.daysToExpire = patron.getHoldDuration();
        this.holdFee = patron.feeForPlacingHold();
    }

    @Override
    public void setStatementValues(PreparedStatement statement) {
        try {
            statement.setLong(1, patron.getId());
            statement.setLong(2, instance.getId());
            statement.setDate(3, java.sql.Date.valueOf(datePlaced));
            statement.setInt(4, daysToExpire);
            statement.setBigDecimal(5, holdFee);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
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
            this.holdFee = resultSet.getBigDecimal("hold_fee");
            this.datePlaced = resultSet.getDate("date_placed").toLocalDate();
            this.daysToExpire = resultSet.getInt("days_to_expire");
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long getId() {
        Assert.state(id != null, "Id not assigned");
        return id;
    }

    @Override
    public boolean hasId() {
        return false;
    }

    public void expireIn(Integer daysToExpire) {
        Assert.state(this.daysToExpire == null, "Hold already expired");
        Assert.state(daysToExpire > 0, "Days to expire must be greater than 0");
        Assert.state(this.datePlaced != null, "Hold not placed yet");
        Assert.state(LocalDate.now().isBefore(this.datePlaced.plusDays(daysToExpire)), "Hold cannot expire in the past");
        this.daysToExpire = daysToExpire;
    }
}
