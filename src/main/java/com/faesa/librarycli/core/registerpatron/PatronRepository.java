package com.faesa.librarycli.core.registerpatron;

import com.faesa.librarycli.core.patronprofile.HeldAmountPerAuthor;
import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatronRepository extends BaseRepository<Patron, Long> {
    List<HeldAmountPerAuthor> findBooksHeldPerAuthor(Patron patron);
}
