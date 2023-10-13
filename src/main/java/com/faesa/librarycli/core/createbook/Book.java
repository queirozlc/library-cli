package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.core.createauthor.Author;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class Book implements DomainValuesExtractor<Long> {

    @Getter
    private Long id;
    private String title;

    private String isbn;

    private LocalDate publicationDate;

    private Integer pages;

    private Author author;

    
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
    public void assignId(Object id) {
        Assert.state(this.id == null, "Id already assigned");
        this.id = Long.parseLong(String.valueOf(id));
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
}
