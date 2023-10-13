package com.faesa.librarycli.core.createbook;

import com.faesa.librarycli.shared.infra.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends BaseRepository<Book, Long> {
    Optional<Book> findByIsbn(String bookIsbn);
}
