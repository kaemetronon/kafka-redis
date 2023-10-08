package com.example.demo.service.request;

import com.example.demo.exceptions.FetchException;
import com.example.demo.model.Author;
import com.example.demo.model.GeoPoint;
import com.example.demo.model.Post;
import com.example.demo.model.dto.UpdatedResultDto;
import com.example.demo.repo.AuthorRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.service.geo.IGeoService;
import com.example.demo.web.client.AuthorClient;
import com.example.demo.web.client.PostClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Slf4j
@Service
public class RequestServiceImpl<T> implements IRequestService<T> {

    private final PostClient postClient;
    private final AuthorClient authorClient;
    private final PostRepository postRepo;
    private final AuthorRepository authorRepo;
    private final KafkaTemplate<String, String> kafka;
    private final IGeoService geoService;

    @Value("${kafka.topic.request}")
    private String requestTopic;

    private final String POST_CLASSNAME = Post.class.getTypeName();
    private final String AUTHOR_CLASSNAME = Author.class.getTypeName();

    @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
    @Override
    public UpdatedResultDto doRequest(Class<T> clazz) {

        String typeName = clazz.getTypeName();
        List response;
        try {
            if (typeName.equals(AUTHOR_CLASSNAME)) {
                response = authorClient.getAuthors();
            } else if (typeName.equals(POST_CLASSNAME))
                response = postClient.getPosts();
            else
                throw new FetchException("Class type didn't match");
        } catch (FeignException e) {
            log.info("Resource didn't fetched");
            throw new FetchException("Resource not found\n" + e);
        }
        log.info("Resource fetched");

        assert response != null;

        if (response.isEmpty()) {
            log.error("Unable to perform request, rollback transaction");
            throw new FetchException("Error\n body: " + response + "\n");
        }
        log.info("Data validated");

//        kafka.send(requestTopic, clazz.getClass().getSimpleName().toUpperCase());
        log.info("Sent into kafka topic: {}", requestTopic);

        UpdatedResultDto result;
        if (typeName.equals(AUTHOR_CLASSNAME))
            result = processAuthor((List<Author>) response);
        else if (typeName.equals(POST_CLASSNAME))
            result = processPost((List<Post>) response);
        else
            throw new IllegalStateException("Unknown class");

        return result;
    }

    private UpdatedResultDto processPost(List<Post> response) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        AtomicReference<Integer> updatedEntities = new AtomicReference<>(0);
        log.info("Processing Post data");

        List<Author> authors = new LinkedList<>();
        List<Integer> authorIds = new LinkedList<>();

        response.forEach(post -> {
            Integer id = post.getAuthorId();
            Post postFromDb = postRepo.findByExternalId(post.getExternalId()).orElse(null);

            if (!authorIds.contains(id)) {
                Author a = authorRepo.findByExternalId(id).orElse(null);
                if (a != null) {
                    authors.add(a);
                    authorIds.add(a.getExternalId());

//                    not to override existing equals posts
                    if (postFromDb == null) {
                        post.setAuthor(a);
                        post.setUpdateDate(now);
                        postRepo.save(post);
                        updatedEntities.set(updatedEntities.get() + 1);
                    } else if (!post.equals(postFromDb)) {
                        post.setAuthor(a);
                        post.setUpdateDate(now);
                        post.setInternalId(postFromDb.getInternalId()); // rewrite data from post to existing cortege in db
                        postRepo.save(post);
                        updatedEntities.set(updatedEntities.get() + 1);
                    }
                } else {
                    log.warn("Unable to save post - related author doesn't exists in db.");
                }
            } else {
                Author a = authors.stream().filter(e -> e.getExternalId().equals(id)).findFirst().orElse(null);
//                    not to override existing equals posts
                if (!post.equals(postFromDb)) {
                    post.setAuthor(a);
                    post.setUpdateDate(now);
                    postRepo.save(post);
                    updatedEntities.set(updatedEntities.get() + 1);
                }
            }
        });
        log.info("Saved {} posts", updatedEntities.get());
        return new UpdatedResultDto(updatedEntities.get(), now);
    }

    private UpdatedResultDto processAuthor(List<Author> response) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        AtomicReference<Integer> updatedEntities = new AtomicReference<>(0);

        log.info("Processing Authors data");

        response.forEach(author -> {
            Integer id = author.getExternalId();
            GeoPoint authorGeo = author.getAddress().getGeo();
            Author authorFromDb = authorRepo.findByExternalId(author.getExternalId()).orElse(null);

            if (author.equals(authorFromDb)
                    && authorFromDb.getAddress().getGeo() != null
                    && authorGeo.equals(authorFromDb.getAddress().getGeo())) {
                log.warn("Unable to save author - id: {} already exists in db.", id);
            } else {
                String countryName = geoService.getCountryName(author, authorGeo);
                author.getAddress().setCountry(countryName);
                author.setUpdateDate(now);
                authorRepo.save(author);
                updatedEntities.set(updatedEntities.get() + 1);
            }
        });
        log.info("Saved {} authors", updatedEntities.get());
        return new UpdatedResultDto(updatedEntities.get(), now);
    }
}
