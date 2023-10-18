package com.faesa.librarycli.core.newinstance;

import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.core.placinghold.Hold;
import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Instance implements DomainValuesExtractor<Long> {

    private final Book book;
    private Long id;
    private InstanceStatus status;
    private InstanceType type;

    public Instance(InstanceStatus status, InstanceType type, Book book) {
        this.status = status;
        this.type = type;
        this.book = book;
    }

    @Override
    public void setStatementValues(PreparedStatement statement) {
        try {
            statement.setString(1, status.name());
            statement.setString(2, type.name());
            statement.setString(3, book.getIsbn());
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
        Assert.state(id != null, "Id not assigned");
        return id;
    }

    @Override
    public boolean hasId() {
        return id != null;
    }

    public boolean acceptsHold(Patron patron) {
        return status == InstanceStatus.AVAILABLE && type.acceptsHold(patron);
    }

    public Hold placeOnHold(Patron patron, int daysToExpire) {
        Assert.isTrue(acceptsHold(patron), "Instance does not accept hold");
        Assert.isTrue(patron.canHold(daysToExpire), "Patron can't hold anymore because he has more than two holds overdue");
        this.status = InstanceStatus.HOLD;
        return new Hold(patron, this);
    }

    public String bookTitle() {
        return book.getTitle();
    }

    public String bookAuthorName() {
        return book.getAuthorName();
    }

    public void checkout() {
        this.status = InstanceStatus.CHECKED_OUT;
    }

    public boolean heldBy(Patron patron) {
        return status == InstanceStatus.HOLD && patron.hasHoldOn(this);
    }

    public boolean isInstanceOf(Book book) {
        return this.book.sameAs(book);
    }

    public void cancelHold() {
        Assert.state(this.isHeld(), "Instance is not on hold");
        this.status = InstanceStatus.AVAILABLE;
    }

    public boolean isHeld() {
        return status == InstanceStatus.HOLD;
    }

    public boolean borrowedBy(Patron patron) {
        return status == InstanceStatus.CHECKED_OUT && patron.hasBorrowed(this);
    }

    public boolean isSameOf(Instance instance) {
        return this.id.equals(instance.id);
    }

    public void instanceReturned() {
        Assert.state(this.isBorrowed(), "Instance is not borrowed");
        this.status = InstanceStatus.AVAILABLE;
    }

    private boolean isBorrowed() {
        return status == InstanceStatus.CHECKED_OUT;
    }
}
