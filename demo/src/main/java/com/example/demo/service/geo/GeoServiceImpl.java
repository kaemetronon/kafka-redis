package com.example.demo.service.geo;

import com.example.demo.model.Author;
import com.example.demo.model.GeoCountry;
import com.example.demo.model.GeoPoint;
import com.example.demo.model.dto.geo.GeoRequestDto;
import com.example.demo.model.dto.geo.GeoResponseDto;
import com.example.demo.web.client.GeoClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoServiceImpl implements IGeoService {

    private final GeoClient geoClient;

    public String getCountryName(Author author, GeoPoint geo) {

        String name = null;
        var lat = geo.getLat();
        Pair<Double, Double> minMaxLatitude = Pair.of(Math.floor(lat), Math.ceil(lat));
        boolean continueWhile;
        GeoResponseDto response;

        do {
            try {
                response = getCountriesByLatitudeScale(minMaxLatitude);
            } catch (FeignException e) {
                log.error(e.getMessage());
                return null;
            }

            var countries = response.getData();
            log.info("{} : {} entities", response.getMsg(), countries.size());
            if (response.getError()) {
                log.error("Unable to get country for author(external id: {})", author.getExternalId());
                return null;
            }

            if (countries.isEmpty()) { // need to expand search
                minMaxLatitude = Pair.of(minMaxLatitude.getFirst() - 5, minMaxLatitude.getSecond() + 5);
                continueWhile = true;
                continue;
            }
            name = getCountryNameFromList(author, countries);
            continueWhile = false;
        } while (continueWhile);
        log.info("Name for Author with externalId: {} was setted", author.getExternalId());
        return name;
    }

    private GeoResponseDto getCountriesByLatitudeScale(Pair<Double, Double> latitudes) {
        GeoRequestDto requestDto = new GeoRequestDto("lat", latitudes.getFirst(), latitudes.getSecond());
        return geoClient.getCountryByLatLong(requestDto);
    }

    private String getCountryNameFromList(Author author, List<GeoCountry> countries) {
        var point = author.getAddress().getGeo();
        var lat = point.getLat();
        var lng = point.getLng();
        var country = countries.stream().min((first, second) ->
                (int) (getDistance(lat, lng, first) - getDistance(lat, lng, second))
        ).get();

        return country.getName();
    }

    private long getDistance(Double lat, Double lng, GeoCountry country) {
        return Math.round(Math.abs(lat - country.getLat()) + Math.abs(lng - country.getLng()));
    }
}
