package com.faesa.librarycli.core.newinstance;

import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.core.placinghold.Hold;
import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

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

    public Hold placeOnHold(Patron patron, UnaryOperator<Instance> onHold) {
        Assert.state(acceptsHold(patron), "Instance does not accept hold");
        this.status = InstanceStatus.HOLD;
        onHold.apply(this);
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
}
