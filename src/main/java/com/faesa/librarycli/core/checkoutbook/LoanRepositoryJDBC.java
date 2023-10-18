package com.faesa.librarycli.core.checkoutbook;

import com.faesa.librarycli.core.createauthor.Author;
import com.faesa.librarycli.core.createbook.Book;
import com.faesa.librarycli.core.newinstance.Instance;
import com.faesa.librarycli.core.newinstance.InstanceStatus;
import com.faesa.librarycli.core.newinstance.InstanceType;
import com.faesa.librarycli.core.placinghold.Hold;
import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.core.registerpatron.PatronType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class LoanRepositoryJDBC implements LoanRepository {
    private final Connection connection;

    @Override
    public Loan save(Loan entity) {
        if (entity.hasId()) return update(entity);
        String sql = "INSERT INTO C##LABDATABASE.loan (hold_id, time, loan_date, due_date, overdue_fee) VALUES (?, ?, ?, ?, ?)";

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
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Loan> findById(Long id) {
        String sql = getFindByQuery("l.id = ?");

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildWithRelations(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM C##LABDATABASE.loan WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Loan> findAll() {
        var loans = new ArrayList<Loan>(Collections.emptyList());
        String sql = getFindQuery();
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                loans.add(buildWithRelations(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM C##LABDATABASE.loan WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int count() {
        final var sql = "SELECT COUNT(*) FROM C##LABDATABASE.loan";
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

    @Override
    public Collection<Loan> findAllPatronCheckouts(Patron patron) {
        var loans = new ArrayList<Loan>(Collections.emptyList());
        String sql = getFindByQuery("p.id = ?");

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, patron.getId());
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                loans.add(buildWithRelations(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    private Loan buildWithRelations(ResultSet resultSet) {
        try {
            final var patronId = resultSet.getString("patron_id");
            final var patronName = resultSet.getString("name");
            final var patronType = resultSet.getString("type");
            final var patron = new Patron(patronName, PatronType.valueOf(patronType));
            patron.assignId(Long.parseLong(patronId));

            final var authorName = resultSet.getString("name");
            final var authorId = resultSet.getString("author_id");
            final var authorNationality = resultSet.getString("nationality");
            var author = new Author(authorName, authorNationality);
            author.assignId(Long.parseLong(authorId));

            final var holdId = resultSet.getString("hold_id");
            final var holdDatePlaced = resultSet.getDate("date_placed").toLocalDate();
            final var holdDaysToExpire = resultSet.getString("days_to_expire");
            final var holdFee = resultSet.getString("hold_fee");

            final var bookId = resultSet.getLong("book_id");
            final var bookIsbn = resultSet.getString("isbn");
            final var bookPages = resultSet.getInt("pages");
            final var bookPublicationDate = resultSet.getDate("publication_date").toLocalDate();
            final var bookTitle = resultSet.getString("title");
            var book = new Book(bookTitle, bookIsbn, bookPublicationDate, bookPages, author);
            book.assignId(bookId);

            var instanceId = resultSet.getLong("instance_id");
            var instanceType = InstanceType.supports(resultSet.getString("type"));
            var instanceStatus = InstanceStatus.valueOf(resultSet.getString("status"));
            var instance = new Instance(instanceStatus, instanceType, book);
            instance.assignId(instanceId);

            var hold = new Hold(patron, instance, holdDatePlaced, Integer.parseInt(holdDaysToExpire), new BigDecimal(holdFee));
            hold.assignId(Long.parseLong(holdId));

            var loan = new Loan(hold, resultSet.getInt("time"), resultSet.getDate("loan_date").toInstant(), resultSet.getDate("due_date").toInstant(), resultSet.getBigDecimal("overdue_fee"));

            return loan;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFindByQuery(String predicate) {
        return """
                SELECT
                	p.id as patron_id,
                	p.name,
                	p.type,
                	h.id as hold_id,
                	h.date_placed,
                	h.days_to_expire,
                	h.hold_fee,
                	b.id as book_id,
                	b.isbn,
                	b.pages,
                	b.publication_date,
                	b.title,
                	i.type,
                	i.id as instance_id,
                	i.status,
                	a.id as author_id,
                	a.name,
                	a.nationality
                FROM
                	C##LABDATABASE.loan l
                	INNER JOIN C##LABDATABASE.hold h ON l.hold_id = h.id
                	INNER JOIN C##LABDATABASE.patron p ON p.id = h.patron_id
                	INNER JOIN C##LABDATABASE.instance i ON i.id = h.instance_id
                	INNER JOIN C##LABDATABASE.book b ON i.book_isbn = b.isbn
                	INNER JOIN C##LABDATABASE.author a ON a.id = b.author_id
                WHERE
                	%s
                """.formatted(predicate);
    }

    private String getFindQuery() {
        return """
                SELECT
                	p.id as patron_id,
                	p.name,
                	p.type,
                	h.id as hold_id,
                	h.date_placed,
                	h.days_to_expire,
                	h.hold_fee,
                	b.id as book_id,
                	b.isbn,
                	b.pages,
                	b.publication_date,
                	b.title,
                	i.type,
                	i.id as instance_id,
                	i.status,
                	a.id as author_id,
                	a.name,
                	a.nationality
                FROM
                	C##LABDATABASE.loan l
                	INNER JOIN C##LABDATABASE.hold h ON l.hold_id = h.id
                	INNER JOIN C##LABDATABASE.patron p ON p.id = h.patron_id
                	INNER JOIN C##LABDATABASE.instance i ON i.id = h.instance_id
                	INNER JOIN C##LABDATABASE.book b ON i.book_isbn = b.isbn
                	INNER JOIN C##LABDATABASE.author a ON a.id = b.author_id
                """;
    }

    private Loan update(Loan entity) {
        String sql = "UPDATE C##LABDATABASE.loan SET hold_id = ?, time = ?, loan_date = ?, due_date = ?, overdue_fee = ? WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            var fields = entity.getClass().getDeclaredFields();
            Arrays.stream(fields).filter(field -> !field.getName().equals("id")).forEach(field -> {
                try {
                    field.setAccessible(true);
                    statement.setObject(field.getInt(entity), field.get(entity));
                } catch (IllegalAccessException | SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            statement.setLong(fields.length + 1, entity.getId());
            statement.executeUpdate();
            connection.commit();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}