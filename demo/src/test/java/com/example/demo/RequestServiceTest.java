package com.example.demo;

import com.example.demo.exceptions.FetchException;
import com.example.demo.model.Address;
import com.example.demo.model.Author;
import com.example.demo.model.GeoPoint;
import com.example.demo.model.Post;
import com.example.demo.repo.AuthorRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.service.geo.GeoServiceImpl;
import com.example.demo.service.geo.IGeoService;
import com.example.demo.service.request.IRequestService;
import com.example.demo.service.request.RequestServiceImpl;
import com.example.demo.web.client.AuthorClient;
import com.example.demo.web.client.PostClient;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.any;

@SuppressWarnings("unchecked")
public class RequestServiceTest extends AbstractTest {

    private PostClient postClient;
    private AuthorClient authorClient;
    private PostRepository postRepository;
    private AuthorRepository authorRepository;
    private KafkaTemplate kafkaTemplate;
    private IRequestService requestService;
    private IGeoService geoService;

    private List<Post> feignPosts = new ArrayList<>() {{
        add(new Post(0, 1, 2, null, null, "t1", "b1"));
    }};
    private List<Author> feignAuthors = new ArrayList<>() {{
        add(new Author(0, 1, null, null, new Address() {{
            setGeo(new GeoPoint(){{
                setLat(1d);
                setLng(2d);
            }});
        }}, "n1", null, null, null, null));
    }};
    private List<Post> dbPosts = new ArrayList<>() {{
        add(new Post(0, 2, 2, null, null, "t2", "b2"));
        add(new Post(0, 3, 2, null, null, "t3", "b3"));
    }};
    private List<Author> dbAuthors = new ArrayList<>() {{
        add(new Author(0, 2, null, null, new Address() {{
            setGeo(new GeoPoint(){{
                setLat(3d);
                setLng(4d);
            }});
        }}, "n2", null, null, null, null));
    }};

    @BeforeEach
    public void beforeEach() {
        postClient = mock(PostClient.class);
        authorClient = mock(AuthorClient.class);
        postRepository = mock(PostRepository.class);
        authorRepository = mock(AuthorRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        geoService = mock(GeoServiceImpl.class);
        requestService = new RequestServiceImpl(postClient, authorClient, postRepository, authorRepository, kafkaTemplate, geoService);
    }

    @AfterEach
    public void afterEach() {
        reset(postClient);
        reset(authorClient);
        reset(postRepository);
        reset(authorRepository);
    }

    @Test
    public void doRequestMethodOkTest() {
        doReturn(feignPosts).when(postClient).getPosts();
        doReturn(feignAuthors).when(authorClient).getAuthors();
        doReturn(null).when(kafkaTemplate).send(anyString(), anyString());
        doReturn("ROSSIA").when(geoService).getCountryName(any(Author.class), any(GeoPoint.class));
        for (Post post : dbPosts)
            doReturn(Optional.of(post)).when(postRepository).findByExternalId(post.getExternalId());
        for (Author author : dbAuthors)
            doReturn(Optional.of(author)).when(authorRepository).findByExternalId(author.getExternalId());

        var res1 = requestService.doRequest(Post.class);
        var res2 = requestService.doRequest(Author.class);

        assertEquals(feignPosts.size(), res1.getTotalCount());
        assertEquals(feignAuthors.size(), res2.getTotalCount());
    }

    @Test
    public void errorClassTest() {
        assertThrows(FetchException.class, () -> requestService.doRequest(String.class));
    }

    @Test
    public void feignExcTest() {
        doThrow(FeignException.class).when(postClient).getPosts();
        doThrow(FeignException.class).when(authorClient).getAuthors();
        doReturn("ROSSIA").when(geoService).getCountryName(any(Author.class), any(GeoPoint.class));

        assertThrows(FetchException.class, () -> requestService.doRequest(Post.class));
        assertThrows(FetchException.class, () -> requestService.doRequest(Author.class));
    }

    @Test
    public void dbExcTest() {
        doReturn(feignPosts).when(postClient).getPosts();
        doReturn(feignAuthors).when(authorClient).getAuthors();
        doReturn(null).when(kafkaTemplate).send(anyString(), anyString());
        doReturn("ROSSIA").when(geoService).getCountryName(any(Author.class), any(GeoPoint.class));

        for (Post post : Stream.concat(dbPosts.stream(), feignPosts.stream()).collect(Collectors.toList()))
            doThrow(new DataAccessException("DataAccessException") {
            }).when(postRepository).findByExternalId(post.getExternalId());
        for (Author author : Stream.concat(dbAuthors.stream(), feignAuthors.stream()).collect(Collectors.toList()))
            doThrow(new DataAccessException("DataAccessException") {
            }).when(authorRepository).findByExternalId(author.getExternalId());

        assertThrows(DataAccessException.class, () -> requestService.doRequest(Post.class));
        assertThrows(DataAccessException.class, () -> requestService.doRequest(Author.class));
    }
}
