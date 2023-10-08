package com.example.demo.model.dto.geo;

import lombok.Data;

@Data
public class GeoRequestDto {
    private final String type;
    private final Double min;
    private final Double max;
}
