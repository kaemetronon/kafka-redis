package com.example.demo.service.author;

import com.example.demo.exceptions.DTOException;
import com.example.demo.model.Author;
import com.example.demo.model.dto.UpdatedResultDto;
import com.example.demo.repo.AuthorRepository;
import com.example.demo.service.request.IRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
@Slf4j
@Service
public class AuthorServiceImpl implements IAuthorService {

    private final IRequestService<Author> requestService;
    private final AuthorRepository repository;

    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "allauthors", allEntries = true),
                    @CacheEvict(value = "oneauthor", allEntries = true)
            }
    )
    public UpdatedResultDto doSyncAuthors() {
        log.info("Author sync method");
        return requestService.doRequest(Author.class);
    }

    @Override
    @Cacheable("allauthors")
    public List<Author> getAllAuthors() {
        log.info("Into get all authors");
        return (List<Author>) repository.findAll();
    }

    @Override
    @Cacheable(cacheNames = "oneauthor", key = "#id", unless = "#result == null")
    public Author getAuthorByExternalId(Integer id) {
        log.info("Into get one author (external id)");
        var author = repository.findByExternalId(id);
        return author.orElse(null);
    }

    @Transactional
    @Override
    @Caching(
            put = {
                    @CachePut(cacheNames = "oneauthor", key = "#author.externalId")
            },
            evict = {
                    @CacheEvict(value = "allauthors", allEntries = true)
            }
    )
    public Author addAuthor(Author author) {
        log.info("Add author");
        if (repository.existsByExternalId(author.getExternalId()))
            throw new DTOException("Unable to add author. Id already exists.");

        author.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
        return repository.save(author);
    }

    @Transactional
    @Override
    @Caching(
            put = {
                    @CachePut(cacheNames = "oneauthor", key = "#author.externalId")
            },
            evict = {
                    @CacheEvict(value = "allauthors", allEntries = true)
            }
    )
    public Author updateAuthor(Author author) {
        log.info("Update author");
        Optional<Author> authorFromDb = repository.findByExternalId(author.getExternalId());
        if (authorFromDb.isEmpty())
            throw new DTOException("Unable to update author. Author with id: " + author.getExternalId() + " doesn't exists.");

        author.setInternalId(authorFromDb.get().getInternalId());
        author.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
        return repository.save(author);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allauthors", allEntries = true),
            @CacheEvict(value = "oneauthor", key = "#id")
    })
    public void deleteAuthor(Integer id) {
        log.info("Into delete one author");
        if (!repository.existsById(id))
            throw new DTOException("Unable to delete author. Author with id: " + id + " doesn't exists yet.");
        repository.deleteById(id);
    }
}
