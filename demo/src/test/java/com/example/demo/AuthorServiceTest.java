package com.example.demo;

import com.example.demo.model.Address;
import com.example.demo.model.Author;
import com.example.demo.model.GeoPoint;
import com.example.demo.repo.AuthorRepository;
import com.example.demo.service.author.IAuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorServiceTest extends AbstractTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private IAuthorService authorService;

    private List<Author> authors = new ArrayList<>() {
        {
            add(new Author(0, 0, null, null, new Address() {{
                setGeo(new GeoPoint() {{
                    setLat(1d);
                    setLng(2d);
                }});
            }}, "name1", "usrname1", "mail1", "phone1", "site1"));
            add(new Author(1, 1, null, null, new Address() {{
                setGeo(new GeoPoint() {{
                    setLat(3d);
                    setLng(4d);
                }});
            }}, "name2", "usrname2", "mail2", "phone2", "site2"));
        }
    };
    private Author pseudoAuthor = authors.get(0);

    @Transactional
    @Test
    public void findAllTest() {
        List<Author> newAuthors = (List<Author>) authorRepository.saveAll(authors);
        var auth = authorService.getAllAuthors();
        assertEquals(newAuthors.size(), auth.size());
    }

    @Transactional
    @Test
    public void findAuthorByExternalIdTest() {
        Author newAuthor = authorRepository.save(pseudoAuthor);
        Author a = authorService.getAuthorByExternalId(pseudoAuthor.getExternalId());
        assertEquals(newAuthor, a);
    }

    @Transactional
    @Test
    public void addAuthorTest() {
        Author a = authorService.addAuthor(pseudoAuthor);
        assertEquals(pseudoAuthor, a);
    }

    @Transactional
    @Test
    public void updateAuthorTest() {
        String newUsername = "new username";
        authorRepository.save(pseudoAuthor);
        pseudoAuthor.setUsername(newUsername);
        Author a = authorService.updateAuthor(pseudoAuthor);

        assertEquals(pseudoAuthor.getUsername(), a.getUsername());
    }

    @Transactional
    @Test
    public void deleteAuthorTest() {
        List<Author> newAuthors = (List<Author>) authorRepository.saveAll(authors);
        long was = StreamSupport
                .stream(newAuthors.spliterator(), false)
                .count();

        authorService.deleteAuthor(newAuthors.get(0).getInternalId());

        long became = StreamSupport
                .stream(authorRepository.findAll().spliterator(), false)
                .count();

        assertEquals(1, was - became);
    }
}
