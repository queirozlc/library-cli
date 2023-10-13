package com.faesa.librarycli.core.registerpatron;

import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatronRepository extends BaseRepository<Patron, Long> {
}
