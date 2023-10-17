package com.faesa.librarycli.core.checkoutbook;

import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.placinghold.Hold;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Loan implements DomainValuesExtractor<Long> {

    private final Hold hold;
    private Long id;
    private Integer time;

    private Instant loanDate;

    private Instant dueDate;

    private BigDecimal overdueFee;

    /**
     * <p>
     * Loan's are built by held books, so the constructor receives a current hold.
     * </p>
     *
     * @param hold The instance of the book that is being held.
     * @param time The time in days that the book will be borrowed.
     */
    public Loan(Hold hold, Integer time) {
        this.time = time;
        this.hold = hold;
        this.loanDate = Instant.now();
        this.dueDate = Instant.now().plus(time, ChronoUnit.DAYS);
        this.overdueFee = BigDecimal.ZERO;
    }

    public Loan(Hold hold, Integer time, Instant loanDate, Instant dueDate, BigDecimal overdueFee) {
        this.hold = hold;
        this.time = time;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.overdueFee = overdueFee;
    }

    public boolean isOverdue() {
        return Instant.now().isAfter(dueDate);
    }

    @Override
    public void setStatementValues(PreparedStatement statement) {
        try {
            statement.setLong(1, hold.getId());
            statement.setInt(2, time);
            statement.setTimestamp(3, java.sql.Timestamp.from(loanDate));
            statement.setTimestamp(4, java.sql.Timestamp.from(dueDate));
            statement.setBigDecimal(5, overdueFee);
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

    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public boolean hasId() {
        return false;
    }

    public boolean wasReturned() {
        // check if the due date has passed and the overdue fee is still zero
        // if so, that means the book was returned on time
        return Instant.now().isAfter(dueDate) && overdueFee.equals(BigDecimal.ZERO);
    }

    public Instance currentInstance() {
        return hold.getInstance();
    }
}
