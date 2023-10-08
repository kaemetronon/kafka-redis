package com.example.demo.web.client;

import com.example.demo.model.Author;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "author", url = "${external.api.entities}")
public interface AuthorClient {

    @GetMapping("/users")
    List<Author> getAuthors();
}
