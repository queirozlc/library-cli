package com.faesa.librarycli.core.placinghold;

import com.faesa.librarycli.core.registerpatron.Patron;
import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface HoldRepository extends BaseRepository<Hold, Long> {

    Collection<Hold> findAllPatronHolds(Patron patron);
}
