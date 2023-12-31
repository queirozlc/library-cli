package com.faesa.librarycli.core.placinghold;

import com.faesa.librarycli.core.createauthor.Author;
import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.newinstance.InstanceStatus;
import com.faesa.librarycli.core.newinstance.InstanceType;
import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.core.registerpatron.PatronType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HoldRepositoryJDBC implements HoldRepository {

    private final Connection connection;

    @Override
    public Hold save(Hold entity) {
        if (entity.hasId()) {
            return updateExistent(entity);
        }
        String sql = "INSERT INTO C##LABDATABASE.hold (patron_id, instance_id, date_placed, days_to_expire, hold_fee) VALUES (?, ?, ?, ?, ?)";
        try (var statement = connection.prepareStatement(sql, new String[]{"id"})) {
            connection.setAutoCommit(false);
            entity.setStatementValues(statement);
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.assignId(generatedKeys.getLong(1));
            }
            connection.commit();
            return entity;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Hold> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long aLong) {
        String sql = "DELETE FROM C##LABDATABASE.hold WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            statement.setLong(1, aLong);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Collection<Hold> findAll() {
        final var sql = """
                        SELECT H.id AS hold_id,
                               H.date_placed,
                               H.days_to_expire,
                               H.hold_fee,
                               I.type   AS instance_type,
                               I.status AS instance_status,
                               I.id     AS instance_id,
                               B.isbn   AS book_isbn,
                               B.id     AS book_id,
                               B.pages,
                               B.publication_date,
                               B.title,
                               A.name,
                               A.nationality,
                               A.id AS author_id,
                               P.id AS patron_id,
                               P.name AS patron_name,
                               P.type AS patron_type
                        FROM C##LABDATABASE.HOLD H
                        INNER JOIN C##LABDATABASE.INSTANCE I ON I.id = H.instance_id
                        INNER JOIN C##LABDATABASE.BOOK B ON B.isbn = I.book_isbn
                        INNER JOIN C##LABDATABASE.AUTHOR A ON A.id = B.author_id
                        INNER JOIN C##LABDATABASE.PATRON P ON P.id = H.patron_id
                """;

        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            var holds = new ArrayList<Hold>();
            while (resultSet.next()) {
                holds.add(buildHold(resultSet));
            }
            return holds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Hold buildHold(ResultSet resultSet) {
        try {
            var instanceType = resultSet.getString("instance_type");
            var instanceStatus = resultSet.getString("instance_status");
            var bookIsbn = resultSet.getString("book_isbn");
            var bookId = resultSet.getLong("book_id");
            var pages = resultSet.getInt("pages");
            var publicationDate = resultSet.getDate("publication_date").toLocalDate();
            var title = resultSet.getString("title");
            var authorName = resultSet.getString("name");
            var nationality = resultSet.getString("nationality");
            var authorId = resultSet.getLong("author_id");
            var holdId = resultSet.getLong("hold_id");
            var datePlaced = resultSet.getDate("date_placed").toLocalDate();
            var daysToExpire = resultSet.getInt("days_to_expire");
            var holdFee = resultSet.getBigDecimal("hold_fee");


            var author = new Author(authorName, nationality);
            author.assignId(authorId);

            var book = new Book(title, bookIsbn, publicationDate, pages, author);
            book.assignId(bookId);

            var instance = new Instance(InstanceStatus.valueOf(instanceStatus), InstanceType.supports(instanceType), book);
            instance.assignId(resultSet.getLong("instance_id"));

            final var patron = new Patron(
                    resultSet.getString("patron_name"),
                    PatronType.supports(resultSet.getString("patron_type"))
            );
            patron.assignId(resultSet.getLong("patron_id"));
            var hold = new Hold(patron, instance, datePlaced, daysToExpire, holdFee);
            hold.assignId(holdId);
            return hold;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM C##LABDATABASE.hold WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int count() {
        final var sql = "SELECT COUNT(*) FROM C##LABDATABASE.hold";
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private Hold updateExistent(Hold entity) {
        String sql = "UPDATE C##LABDATABASE.hold SET patron_id = ?, instance_id = ?, date_placed = ?, days_to_expire = ?, hold_fee = ? WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            entity.setStatementValues(statement);
            statement.setLong(6, entity.getId());
            statement.executeUpdate();
            connection.commit();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Collection<Hold> findAllPatronHolds(Patron patron) {
        var holds = new ArrayList<>(Collections.<Hold>emptyList());
        var sql = """
                SELECT
                    H.id AS hold_id,
                    H.date_placed,
                    H.days_to_expire,
                    H.hold_fee,
                    I.type AS instance_type,
                    I.status AS instance_status,
                    I.id AS instance_id,
                    B.isbn AS book_isbn,
                    B.id AS book_id,
                    B.pages,
                    B.publication_date,
                    B.title,
                    A.name,
                    A.nationality,
                    A.id AS author_id
                FROM
                    C##LABDATABASE.HOLD H
                INNER JOIN C##LABDATABASE.INSTANCE I ON I.id = H.instance_id
                INNER JOIN C##LABDATABASE.BOOK B ON B.isbn = I.book_isbn
                INNER JOIN C##LABDATABASE.AUTHOR A ON A.id = B.author_id
                WHERE
                    h.patron_id = ?
                """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, patron.getId());
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                holds.add(buildHold(resultSet, patron));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return holds;
    }

    private Hold buildHold(ResultSet resultSet, Patron patron) {
        try {
            var instanceType = resultSet.getString("instance_type");
            var instanceStatus = resultSet.getString("instance_status");
            var bookIsbn = resultSet.getString("book_isbn");
            var bookId = resultSet.getLong("book_id");
            var pages = resultSet.getInt("pages");
            var publicationDate = resultSet.getDate("publication_date").toLocalDate();
            var title = resultSet.getString("title");
            var authorName = resultSet.getString("name");
            var nationality = resultSet.getString("nationality");
            var authorId = resultSet.getLong("author_id");
            var holdId = resultSet.getLong("hold_id");
            var datePlaced = resultSet.getDate("date_placed").toLocalDate();
            var daysToExpire = resultSet.getInt("days_to_expire");
            var holdFee = resultSet.getBigDecimal("hold_fee");


            var author = new Author(authorName, nationality);
            author.assignId(authorId);

            var book = new Book(title, bookIsbn, publicationDate, pages, author);
            book.assignId(bookId);

            var instance = new Instance(InstanceStatus.valueOf(instanceStatus), InstanceType.supports(instanceType), book);
            instance.assignId(resultSet.getLong("instance_id"));
            var hold = new Hold(patron, instance, datePlaced, daysToExpire, holdFee);
            hold.assignId(holdId);
            return hold;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
