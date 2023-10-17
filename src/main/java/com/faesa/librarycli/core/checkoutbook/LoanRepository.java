package com.faesa.librarycli.core.checkoutbook;

import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
    Collection<Loan> findAllPatronCheckouts(Patron patron);
}
