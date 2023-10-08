package com.example.demo.model.dto.geo;

import com.example.demo.model.GeoCountry;
import lombok.Data;

import java.util.List;

@Data
public class GeoResponseDto {
    private final Boolean error;
    private final String msg;
    private final List<GeoCountry> data;
}
