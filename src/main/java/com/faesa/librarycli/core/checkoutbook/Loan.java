package com.faesa.librarycli.core.checkoutbook;

import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.placinghold.Hold;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;

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
            statement.setTimestamp(3, Timestamp.from(loanDate));
            statement.setTimestamp(4, Timestamp.from(dueDate));
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
        Assert.state(id != null, "Id not assigned");
        return id;
    }

    @Override
    public boolean hasId() {
        return Objects.nonNull(id);
    }

    public boolean wasReturned() {
        // The book is returned when the due date is before the current date
        // and the overdue fee is zero.
        // If the overdue fee is not zero, the
        // book is overdue and the patron must pay the fee.
        return dueDate.isBefore(Instant.now()) && overdueFee.compareTo(BigDecimal.ZERO) == 0;
    }

    public Instance currentInstance() {
        return hold.getInstance();
    }

    public boolean borrowedInstanceMatches(Instance instance) {
        return this.currentInstance().isSameOf(instance);
    }

    public boolean isBorrowedInstanceOf(Book book) {
        return this.currentInstance().isInstanceOf(book);
    }

    public void returnBook() {
        Assert.state(!wasReturned(), "Book already returned");

        if (isOverdue()) {
            var exceededDays = Instant.now().until(dueDate, ChronoUnit.DAYS);
            this.overdueFee = hold.patronsFeeForOverdueLoan().multiply(BigDecimal.valueOf(exceededDays));
        } else {
            this.overdueFee = BigDecimal.ZERO;
        }

        this.dueDate = Instant.now();
        this.hold.instanceReturned();
    }

    public boolean heldMatches(Hold hold) {
        return this.hold.getId().equals(hold.getId());
    }

    public boolean anyHeldMatches(Set<Hold> holds) {
        return holds.stream().anyMatch(this::heldMatches);
    }
}
