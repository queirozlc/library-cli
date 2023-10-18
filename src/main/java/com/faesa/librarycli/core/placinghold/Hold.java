package com.faesa.librarycli.core.placinghold;

import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Hold implements DomainValuesExtractor<Long> {
    private final Patron patron;
    @Getter
    private final Instance instance;
    private Long id;

    @Getter
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

    public Hold(Patron patron, Instance instance, LocalDate datePlaced, Integer daysToExpire, BigDecimal holdFee) {
        this.patron = patron;
        this.instance = instance;
        this.datePlaced = datePlaced;
        this.daysToExpire = daysToExpire;
        this.holdFee = holdFee;
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
        return Objects.nonNull(id);
    }

    public Long getInstanceId() {
        return this.instance.getId();
    }

    public String getBookTitle() {
        return this.instance.bookTitle();
    }

    public Integer getExpirationDays() {
        return this.daysToExpire;
    }

    public Double getFee() {
        return this.holdFee.doubleValue();
    }

    public String bookAuthorName() {
        return this.instance.bookAuthorName();
    }

    public boolean isExpired() {
        int holdExpirationDays = Optional.ofNullable(this.daysToExpire).orElse(0);
        return LocalDate.now().isAfter(this.datePlaced.plusDays(holdExpirationDays));
    }

    public void acceptCheckout() {
        Assert.state(!isExpired(), "Hold is expired");
        this.instance.checkout();
    }


    public boolean heldInstanceMatches(Instance instance) {
        return this.instance.getId().equals(instance.getId());
    }

    public boolean anyInstanceOf(Book book) {
        return this.instance.isInstanceOf(book);
    }

    public void cancel() {
        this.instance.cancelHold();
    }

    public BigDecimal patronsFeeForOverdueLoan() {
        return this.patron.feeForOverdueLoan();
    }

    public void instanceReturned() {
        this.instance.instanceReturned();
    }
}
