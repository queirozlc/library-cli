package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.core.createauthor.Author;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.placinghold.Hold;
import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.shared.infra.database.RelationshipDomainExtractor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;


public class Book implements RelationshipDomainExtractor<Long> {

    private final Author author;
    private final List<Instance> copies = new ArrayList<>();
    @Getter
    private Long id;
    @Getter
    private String title;
    @Getter
    private String isbn;
    private LocalDate publicationDate;
    private Integer pages;


    public Book(String title, String isbn, LocalDate publicationDate, Integer pages, Author author) {
        this.title = title;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.pages = pages;
        this.author = author;
    }

    @Override
    public void setStatementValues(PreparedStatement statement) {
        try {
            statement.setString(1, title);
            statement.setString(2, isbn);
            statement.setDate(3, java.sql.Date.valueOf(publicationDate));
            statement.setInt(4, pages);
            statement.setLong(5, author.getId());
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
            this.title = resultSet.getString("title");
            this.isbn = resultSet.getString("isbn");
            this.publicationDate = resultSet.getDate("publication_date").toLocalDate();
            this.pages = resultSet.getInt("pages");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasId() {
        return id != null;
    }

    public boolean canBePlacedOnHold(Patron patron) {
        return copies.stream().anyMatch(copy -> copy.acceptsHold(patron)) && patron.canHold();
    }

    public Hold placeOnHold(Patron patron, UnaryOperator<Instance> onHold) {
        return copies.stream()
                .filter(copy -> copy.acceptsHold(patron))
                .findFirst()
                .map(copy -> {
                    Hold hold = copy.placeOnHold(patron);
                    onHold.apply(copy);
                    return hold;
                })
                .orElseThrow(() -> new RuntimeException("No copy available"));
    }

    @Override
    public void fromResultSetWithRelationships(ResultSet resultSet) {
    }

    public void addInstance(Instance instance) {
        copies.add(instance);
    }


    public String getAuthorName() {
        return author.getName();
    }
}
