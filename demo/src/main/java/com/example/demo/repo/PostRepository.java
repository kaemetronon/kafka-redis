package com.example.demo.repo;

import com.example.demo.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    Optional<Post> findByExternalId(Integer id);
}