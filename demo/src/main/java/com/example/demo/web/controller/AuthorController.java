package com.example.demo.web.controller;

import com.example.demo.model.Author;
import com.example.demo.model.dto.UpdatedResultDto;
import com.example.demo.service.author.IAuthorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "Author controller")
@RequestMapping("/author")
public class AuthorController {

    private final IAuthorService authorService;

    @GetMapping("/sync")
    @ApiOperation(value = "Downloads actual authors data from external source")
    public ResponseEntity<UpdatedResultDto> authorSync() {
        var res = authorService.doSyncAuthors();
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/all")
    @ApiOperation(value = "Show all authors from db")
    ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/external/{id}")
    @ApiOperation(value = "Find author by external id (external_id is id in jsonplaceholder.typicode.com)")
    ResponseEntity<Author> getAuthorByExternalId(@PathVariable Integer id) {
        Author auth = authorService.getAuthorByExternalId(id);
        return ResponseEntity.ok(auth);
    }

    @PostMapping
    @ApiOperation(value = "Save new author into db")
    public ResponseEntity<Author> addAuthor(@RequestBody Author author) {
        var auth = authorService.addAuthor(author);
        if (auth != null)
            return ResponseEntity.ok(auth);
        else return null;
    }

    @PutMapping
    @ApiOperation(value = "Update existing author in db")
    public ResponseEntity<Author> updateAuthor(@RequestBody Author author) {
        var auth = authorService.updateAuthor(author);
        if (auth != null)
            return ResponseEntity.ok(auth);
        else return null;
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete existing author in db")
    public ResponseEntity<String> deleteAuthor(@PathVariable Integer id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok("Author id: " + id + " was deleted");
    }
}
