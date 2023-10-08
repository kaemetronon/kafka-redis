package com.example.demo;

import com.example.demo.model.Address;
import com.example.demo.model.Author;
import com.example.demo.model.GeoCountry;
import com.example.demo.model.GeoPoint;
import com.example.demo.model.dto.geo.GeoResponseDto;
import com.example.demo.repo.AuthorRepository;
import com.example.demo.service.geo.GeoServiceImpl;
import com.example.demo.service.geo.IGeoService;
import com.example.demo.web.client.GeoClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;

public class GeoServiceTest extends AbstractTest {

    private IGeoService geoService;
    private GeoClient geoClient;

    @Autowired
    private AuthorRepository authorRepo;

    private final Author author = new Author(0, 0, null, null,
            new Address(1l, "name", new GeoPoint(1L, 2d, 3d)),
            "name1", "usrname1", "mail1", "phone1", "site1");
    private final GeoResponseDto response =
            new GeoResponseDto(false, "msg",
                    new ArrayList<>(Collections.singletonList(new GeoCountry("name", 1L, 2L))));

    @BeforeEach
    public void startup() {
        geoClient = mock(GeoClient.class);
        geoService = new GeoServiceImpl(geoClient);
    }

    @AfterEach
    public void close() {
        reset(geoClient);
    }

    @Transactional
    @Test
    public void syncCountriesTest() {
        authorRepo.save(author);
        doReturn(response).when(geoClient).getCountryByLatLong(any());

        var res = geoService.getCountryName(author, author.getAddress().getGeo());

        assertEquals(response.getData().get(0).getName(),
                authorRepo.findByExternalId(author.getExternalId()).orElseThrow(null).getAddress().getCountry());


        assertEquals(response.getData().get(0).getName(), res);
    }
}
