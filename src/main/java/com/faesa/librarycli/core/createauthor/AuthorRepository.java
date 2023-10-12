package com.faesa.librarycli.core.createauthor;

import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends BaseRepository<Author, Long> {
}
