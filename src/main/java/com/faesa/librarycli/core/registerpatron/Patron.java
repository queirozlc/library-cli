package com.faesa.librarycli.core.registerpatron;

import com.faesa.librarycli.core.checkoutbook.Loan;
import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.placinghold.Hold;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class Patron implements DomainValuesExtractor<Long> {
    private final Set<Hold> holds = new LinkedHashSet<>();
    private final Set<Loan> loans = new LinkedHashSet<>();
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

    public boolean canHold() {
        // patrons with more than two overdue books cannot place holds
        return holds.stream().filter(Hold::isExpired).count() < 2;
    }

    public boolean canCheckout() {
        // patrons with overdue checkouts cannot check out new books,
        // also patrons cannot borrow more than a certain number of books at a time
        // the above rule does not apply to researchers
        int unreturnedLoans = (int) loans.stream()
                .filter(loan -> !loan.wasReturned())
                .count();

        return loans.stream().noneMatch(Loan::isOverdue) && !type.maximumLoansExceeded(unreturnedLoans);
    }

    public Loan checkout(Hold hold, int checkoutTime) {
        Assert.isTrue(canCheckout(), "Patron cannot checkout");
        Assert.isTrue(holds.contains(hold), "Patron does not hold this book");
        Loan loan = new Loan(hold, checkoutTime);
        loans.add(loan);
        this.resolveHold(hold);
        return loan;
    }

    public void resolveHold(Hold hold) {
        holds.stream().filter(h -> h.equals(hold))
                .findFirst()
                .ifPresent(Hold::acceptCheckout);
    }

    public void addHold(Hold hold) {
        holds.add(hold);
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    public Loan createCheckout(@NotNull @Positive Long holdId, int checkoutTime) {
        Assert.isTrue(canCheckout(), "Patron cannot checkout");
        Assert.isTrue(canHold(), "Patron cannot place holds");
        return holds.stream()
                .filter(hold -> hold.getId().equals(holdId))
                .findFirst()
                .map(hold -> checkout(hold, checkoutTime))
                .orElseThrow(() -> new RuntimeException("Hold not found"));
    }

    public boolean hasHoldOn(Instance instance) {
        return holds.stream().anyMatch(hold -> hold.heldInstanceMatches(instance));
    }

    public void cancelHold(Book book) {
        holds.stream().filter(hold -> hold.anyInstanceOf(book))
                .findFirst()
                .ifPresent(hold -> {
                    hold.cancel();
                    holds.remove(hold);
                });
    }

    public Long getHoldIdFor(Book book) {
        return holds.stream().filter(hold -> hold.anyInstanceOf(book))
                .findFirst()
                .map(Hold::getId)
                .orElseThrow(() -> new RuntimeException("Hold not found"));
    }

    public Instance getHoldInstanceFor(Book book) {
        return holds.stream().filter(hold -> hold.anyInstanceOf(book))
                .findFirst()
                .map(Hold::getInstance)
                .orElseThrow(() -> new RuntimeException("Hold not found"));
    }
}
