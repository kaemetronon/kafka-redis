package com.example.demo.service.post;

import com.example.demo.model.Post;
import com.example.demo.model.dto.UpdatedResultDto;
import com.example.demo.service.request.IRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostServiceImpl implements IPostService {

    private final IRequestService<Post> requestService;

    @Transactional
    @Override
    public UpdatedResultDto doSyncPosts() {
        log.info("Post sync method");
        return requestService.doRequest(Post.class);
    }
}
