package com.faesa.librarycli.core.placinghold;

import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoldRepository extends BaseRepository<Hold, Long> {
}
