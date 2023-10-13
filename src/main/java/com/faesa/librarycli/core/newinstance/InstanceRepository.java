package com.faesa.librarycli.core.newinstance;

import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceRepository extends BaseRepository<Instance, Long> {
}
