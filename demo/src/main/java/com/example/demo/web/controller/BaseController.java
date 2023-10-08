package com.example.demo.web.controller;

import com.example.demo.model.dto.UpdatedResultDto;
import com.example.demo.service.post.IPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Api(value = "Base controller")
public class BaseController {

    private final IPostService postService;

    @GetMapping("/mirror")
    @ApiOperation(value = "Mirror")
    public ResponseEntity<String> mirror(@RequestParam String text) {
        return ResponseEntity.ok().body(text);
    }

    @GetMapping("/post/sync")
    @ApiOperation(value = "Downloads actual posts data from external source")
    public ResponseEntity<UpdatedResultDto> postSync() {
        var res = postService.doSyncPosts();
        return ResponseEntity.ok().body(res);
    }
}