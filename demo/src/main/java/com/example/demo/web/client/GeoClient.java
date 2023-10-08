package com.example.demo.web.client;

import com.example.demo.model.dto.geo.GeoRequestDto;
import com.example.demo.model.dto.geo.GeoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "geo", url = "${external.api.geo}")
public interface GeoClient {

    @PostMapping()
    GeoResponseDto getCountryByLatLong(GeoRequestDto dto);
}
