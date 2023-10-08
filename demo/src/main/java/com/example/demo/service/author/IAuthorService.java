package com.example.demo.service.author;

import com.example.demo.model.Author;
import com.example.demo.model.dto.UpdatedResultDto;

import java.util.List;

public interface IAuthorService {
    UpdatedResultDto doSyncAuthors();

    List<Author> getAllAuthors();

    Author getAuthorByExternalId(Integer id);

    Author addAuthor(Author author);

    Author updateAuthor(Author author);

    void deleteAuthor(Integer id);
}
