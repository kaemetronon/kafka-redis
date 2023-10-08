package com.example.demo;

import com.example.demo.model.Author;
import com.example.demo.repo.AuthorRepository;
import com.example.demo.service.author.AuthorServiceImpl;
import com.example.demo.service.author.IAuthorService;
import com.example.demo.service.request.RequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;


public class CacheTest extends AbstractTest {

    /**
     * IDK how to check amount of started methods - i can see it in logs
     */


    @Autowired
    private IAuthorService authorService;

    private RequestServiceImpl<Author> requestService;
    private AuthorRepository authorRepository;

    private List<Author> authors = new ArrayList<>() {
        {
            add(new Author(1, 1, null, null, null, "name1",
                    "usrname1", "mail1", "phone1", "site1"));
            add(new Author(2, 2, null, null, null, "name2",
                    "usrname2", "mail2", "phone2", "site2"));
            add(new Author(3, 3, null, null, null, "name3",
                    "usrname3", "mail3", "phone3", "site3"));
        }
    };

    @Transactional
    @Test
    public void cacheAllTest() {
        authorService.getAllAuthors();
        authorService.getAllAuthors();
        authorService.getAllAuthors();
        authorService.getAllAuthors();
    }

    @Transactional
    @Test
    public void cacheByIdTest() {
        Author author = authors.get(0);
        Integer id = author.getExternalId();
        authorService.addAuthor(author);

        authorService.getAuthorByExternalId(id);
        authorService.getAuthorByExternalId(id);
        authorService.getAuthorByExternalId(id);
        authorService.getAuthorByExternalId(id);
    }

    @Transactional
    @Test
    public void evictBySyncTest() {
        requestService = mock(RequestServiceImpl.class);
        authorRepository = mock(AuthorRepository.class);
        authorService = new AuthorServiceImpl(requestService, authorRepository);
        doReturn(null).when(requestService).doRequest(Author.class);
        doReturn(null).when(authorRepository).findAll();

        authorService.getAllAuthors();
        authorService.getAllAuthors();

        authorService.doSyncAuthors();

        authorService.getAllAuthors();
        authorService.getAllAuthors();

        reset(requestService);
    }

    @Transactional
    @Test
    public void evictByAddTest() {
        authorService.getAllAuthors();
        authorService.getAllAuthors();

        authorService.addAuthor(authors.get(0));

        authorService.getAllAuthors();
        authorService.getAllAuthors();
    }

    @Transactional
    @Test
    public void evictByUpdateTest() {
        authorService.getAllAuthors();
        authorService.getAllAuthors();

        authorService.addAuthor(authors.get(0));
        authorService.updateAuthor(authors.get(0));

        authorService.getAllAuthors();
        authorService.getAllAuthors();
    }

    @Transactional
    @Test
    public void evictByDelete() {

        Author author = authorService.addAuthor(authors.get(0));

        authorService.getAllAuthors();
        authorService.getAllAuthors();

        authorService.getAuthorByExternalId(author.getExternalId());
        authorService.getAuthorByExternalId(author.getExternalId());

        authorService.deleteAuthor(author.getInternalId());

        authorService.getAllAuthors();
        authorService.getAllAuthors();

        authorService.getAuthorByExternalId(author.getExternalId());
        authorService.getAuthorByExternalId(author.getExternalId());
    }
}
