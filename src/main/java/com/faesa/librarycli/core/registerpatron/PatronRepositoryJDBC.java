package com.faesa.librarycli.core.registerpatron;

import com.faesa.librarycli.core.checkoutbook.LoanRepository;
import com.faesa.librarycli.core.patronprofile.HeldAmountPerAuthor;
import com.faesa.librarycli.core.placinghold.HoldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class PatronRepositoryJDBC implements PatronRepository {

    private final Connection connection;
    private final LoanRepository loanRepository;
    private final HoldRepository holdRepository;

    @Override
    public Patron save(Patron entity) {
        if (entity.hasId()) {
            return update(entity);
        }

        String sql = "INSERT INTO C##LABDATABASE.patron (name, type) VALUES (?, ?)";
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Patron> findById(Long id) {
        String sql = "SELECT * FROM C##LABDATABASE.patron WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildPatron(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private Patron buildPatron(ResultSet resultSet) {
        try {
            var patron = new Patron(
                    resultSet.getString("name"),
                    PatronType.valueOf(resultSet.getString("type"))
            );
            patron.assignId(resultSet.getLong("id"));
            holdRepository.findAllPatronHolds(patron).forEach(patron::addHold);
            loanRepository.findAllPatronCheckouts(patron).forEach(patron::addLoan);
            return patron;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM C##LABDATABASE.patron WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            statement.setLong(1, id);
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Patron> findAll() {
        String sql = "SELECT * FROM C##LABDATABASE.patron";
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            var patrons = new ArrayList<Patron>(Collections.emptyList());
            while (resultSet.next()) {
                patrons.add(buildPatron(resultSet));
            }
            return patrons;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM C##LABDATABASE.patron WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public int count() {
        final var sql = "SELECT COUNT(*) FROM C##LABDATABASE.patron";
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

    private Patron update(Patron entity) {
        String sql = "UPDATE C##LABDATABASE.patron SET name = ?, type = ? WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            entity.setStatementValues(statement);
            statement.setLong(3, entity.getId());
            statement.executeUpdate();
            connection.commit();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<HeldAmountPerAuthor> findBooksHeldPerAuthor(Patron patron) {
        final var sql = """
                SELECT
                    A.NAME,
                    COUNT(*) AS NUMBER_OF_BOOKS_HELD
                FROM C##LABDATABASE.HOLD H
                INNER JOIN C##LABDATABASE.INSTANCE I ON H.INSTANCE_ID = I.ID
                INNER JOIN C##LABDATABASE.BOOK B ON I.BOOK_ISBN = B.ISBN
                INNER JOIN C##LABDATABASE.AUTHOR A ON B.AUTHOR_ID = A.ID
                INNER JOIN C##LABDATABASE.PATRON P on P.ID = H.PATRON_ID
                WHERE P.ID = ?
                GROUP BY A.NAME
                ORDER BY NUMBER_OF_BOOKS_HELD DESC
                """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, patron.getId());
            var resultSet = statement.executeQuery();
            var result = new ArrayList<HeldAmountPerAuthor>();
            while (resultSet.next()) {
                result.add(new HeldAmountPerAuthor(
                        resultSet.getString("name"),
                        resultSet.getInt("number_of_books_held")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
