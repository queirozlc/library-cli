package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.core.createauthor.Author;
import com.faesa.librarycli.shared.core.domain.EntityClass;
import com.faesa.librarycli.shared.infra.database.DomainValuesExtractor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@EntityClass(tableName = "book")
public class Book implements DomainValuesExtractor<Long> {

    @Getter
    private Long id;
    private String title;

    private String isbn;

    private LocalDate publicationDate;

    private Integer pages;

    @EntityClass.Column(name = "author_id")
    @EntityClass.hasOne(foreignKey = "id")
    private Author author;

    /**
     * @see com.faesa.librarycli.shared.core.adapters.AbstractSimpleJDBCRepository
     * @deprecated Used only by AbstractSimpleJDBCRepository to instantiate objects from the database using reflection
     */
    @Deprecated(forRemoval = true)
    protected Book() {
    }

    public Book(String title, String isbn, LocalDate publicationDate, Integer pages, Author author) {
        this.title = title;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.pages = pages;
        this.author = author;
    }

    @Override
    public void extract(PreparedStatement statement) {
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
}
