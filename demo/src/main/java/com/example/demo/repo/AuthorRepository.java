package com.example.demo.repo;

import com.example.demo.model.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Integer> {
    Optional<Author> findByExternalId(Integer id);

    boolean existsByExternalId(Integer id);
}
