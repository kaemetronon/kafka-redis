package com.example.demo.web.client;

import com.example.demo.model.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "post", url = "${external.api.entities}")
public interface PostClient {

    @GetMapping("/posts")
    List<Post> getPosts();

}
